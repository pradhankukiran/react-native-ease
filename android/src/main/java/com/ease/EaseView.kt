package com.ease

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.facebook.react.views.view.ReactViewGroup
import kotlin.math.sqrt

class EaseView(context: Context) : ReactViewGroup(context) {

    // --- Previous animate values (for change detection) ---
    private var prevOpacity: Float? = null
    private var prevTranslateX: Float? = null
    private var prevTranslateY: Float? = null
    private var prevScale: Float? = null
    private var prevRotate: Float? = null

    // --- First mount tracking ---
    private var isFirstMount: Boolean = true

    // --- Transition config (set by ViewManager) ---
    var transitionType: String = "timing"
    var transitionDuration: Int = 300
    var transitionEasing: String = "easeInOut"
    var transitionDamping: Float = 15.0f
    var transitionStiffness: Float = 120.0f
    var transitionMass: Float = 1.0f

    // --- Initial animate values (set by ViewManager) ---
    var initialAnimateOpacity: Float = 1.0f
    var initialAnimateTranslateX: Float = 0.0f
    var initialAnimateTranslateY: Float = 0.0f
    var initialAnimateScale: Float = 1.0f
    var initialAnimateRotate: Float = 0.0f

    // --- Pending animate values (buffered per-view, applied in onAfterUpdateTransaction) ---
    var pendingOpacity: Float = 1.0f
    var pendingTranslateX: Float = 0.0f
    var pendingTranslateY: Float = 0.0f
    var pendingScale: Float = 1.0f
    var pendingRotate: Float = 0.0f

    // --- Running animations ---
    private val runningAnimators = mutableMapOf<String, ObjectAnimator>()
    private val runningSpringAnimations = mutableMapOf<DynamicAnimation.ViewProperty, SpringAnimation>()

    // --- Easing interpolators ---
    private fun getInterpolator(easing: String): TimeInterpolator = when (easing) {
        "easeIn" -> PathInterpolator(0.42f, 0f, 1.0f, 1.0f)
        "easeOut" -> PathInterpolator(0.0f, 0.0f, 0.58f, 1.0f)
        "easeInOut" -> PathInterpolator(0.42f, 0f, 0.58f, 1.0f)
        "linear" -> LinearInterpolator()
        else -> PathInterpolator(0.42f, 0f, 0.58f, 1.0f)
    }

    fun applyPendingAnimateValues() {
        applyAnimateValues(pendingOpacity, pendingTranslateX, pendingTranslateY, pendingScale, pendingRotate)
    }

    private fun applyAnimateValues(
        opacity: Float,
        translateX: Float,
        translateY: Float,
        scale: Float,
        rotate: Float
    ) {
        if (isFirstMount) {
            isFirstMount = false

            val hasInitialAnimation =
                initialAnimateOpacity != opacity ||
                initialAnimateTranslateX != translateX ||
                initialAnimateTranslateY != translateY ||
                initialAnimateScale != scale ||
                initialAnimateRotate != rotate

            if (hasInitialAnimation) {
                this.alpha = initialAnimateOpacity
                this.translationX = initialAnimateTranslateX
                this.translationY = initialAnimateTranslateY
                this.scaleX = initialAnimateScale
                this.scaleY = initialAnimateScale
                this.rotation = initialAnimateRotate

                if (initialAnimateOpacity != opacity) {
                    animateProperty("alpha", DynamicAnimation.ALPHA, initialAnimateOpacity, opacity)
                } else {
                    this.alpha = opacity
                }

                if (initialAnimateTranslateX != translateX) {
                    animateProperty("translationX", DynamicAnimation.TRANSLATION_X, initialAnimateTranslateX, translateX)
                } else {
                    this.translationX = translateX
                }

                if (initialAnimateTranslateY != translateY) {
                    animateProperty("translationY", DynamicAnimation.TRANSLATION_Y, initialAnimateTranslateY, translateY)
                } else {
                    this.translationY = translateY
                }

                if (initialAnimateScale != scale) {
                    animateProperty("scaleX", DynamicAnimation.SCALE_X, initialAnimateScale, scale)
                    animateProperty("scaleY", DynamicAnimation.SCALE_Y, initialAnimateScale, scale)
                } else {
                    this.scaleX = scale
                    this.scaleY = scale
                }

                if (initialAnimateRotate != rotate) {
                    animateProperty("rotation", DynamicAnimation.ROTATION, initialAnimateRotate, rotate)
                } else {
                    this.rotation = rotate
                }
            } else {
                this.alpha = opacity
                this.translationX = translateX
                this.translationY = translateY
                this.scaleX = scale
                this.scaleY = scale
                this.rotation = rotate
            }
        } else {
            if (prevOpacity != null && prevOpacity != opacity) {
                val from = getCurrentValue("alpha")
                animateProperty("alpha", DynamicAnimation.ALPHA, from, opacity)
            }

            if (prevTranslateX != null && prevTranslateX != translateX) {
                val from = getCurrentValue("translationX")
                animateProperty("translationX", DynamicAnimation.TRANSLATION_X, from, translateX)
            }

            if (prevTranslateY != null && prevTranslateY != translateY) {
                val from = getCurrentValue("translationY")
                animateProperty("translationY", DynamicAnimation.TRANSLATION_Y, from, translateY)
            }

            if (prevScale != null && prevScale != scale) {
                val fromX = getCurrentValue("scaleX")
                val fromY = getCurrentValue("scaleY")
                animateProperty("scaleX", DynamicAnimation.SCALE_X, fromX, scale)
                animateProperty("scaleY", DynamicAnimation.SCALE_Y, fromY, scale)
            }

            if (prevRotate != null && prevRotate != rotate) {
                val from = getCurrentValue("rotation")
                animateProperty("rotation", DynamicAnimation.ROTATION, from, rotate)
            }
        }

        prevOpacity = opacity
        prevTranslateX = translateX
        prevTranslateY = translateY
        prevScale = scale
        prevRotate = rotate
    }

    private fun getCurrentValue(propertyName: String): Float = when (propertyName) {
        "alpha" -> this.alpha
        "translationX" -> this.translationX
        "translationY" -> this.translationY
        "scaleX" -> this.scaleX
        "scaleY" -> this.scaleY
        "rotation" -> this.rotation
        else -> 0f
    }

    private fun animateProperty(
        propertyName: String,
        viewProperty: DynamicAnimation.ViewProperty,
        fromValue: Float,
        toValue: Float
    ) {
        if (transitionType == "spring") {
            animateSpring(viewProperty, toValue)
        } else {
            animateTiming(propertyName, fromValue, toValue)
        }
    }

    private fun animateTiming(propertyName: String, fromValue: Float, toValue: Float) {
        cancelSpringForProperty(propertyName)
        runningAnimators[propertyName]?.cancel()

        val animator = ObjectAnimator.ofFloat(this, propertyName, fromValue, toValue).apply {
            duration = transitionDuration.toLong()
            interpolator = getInterpolator(transitionEasing)
        }

        runningAnimators[propertyName] = animator
        animator.start()
    }

    private fun animateSpring(viewProperty: DynamicAnimation.ViewProperty, toValue: Float) {
        cancelTimingForViewProperty(viewProperty)

        val existingSpring = runningSpringAnimations[viewProperty]
        if (existingSpring != null && existingSpring.isRunning) {
            existingSpring.animateToFinalPosition(toValue)
            return
        }

        val dampingRatio = (transitionDamping / (2.0f * sqrt(transitionStiffness * transitionMass)))
            .coerceAtLeast(0.01f)

        val spring = SpringAnimation(this, viewProperty).apply {
            spring = SpringForce(toValue).apply {
                this.dampingRatio = dampingRatio
                this.stiffness = transitionStiffness
            }
        }

        runningSpringAnimations[viewProperty] = spring
        spring.start()
    }

    private fun cancelTimingForViewProperty(viewProperty: DynamicAnimation.ViewProperty) {
        val propertyName = when (viewProperty) {
            DynamicAnimation.ALPHA -> "alpha"
            DynamicAnimation.TRANSLATION_X -> "translationX"
            DynamicAnimation.TRANSLATION_Y -> "translationY"
            DynamicAnimation.SCALE_X -> "scaleX"
            DynamicAnimation.SCALE_Y -> "scaleY"
            DynamicAnimation.ROTATION -> "rotation"
            else -> return
        }
        runningAnimators[propertyName]?.cancel()
        runningAnimators.remove(propertyName)
    }

    private fun cancelSpringForProperty(propertyName: String) {
        val viewProperty = when (propertyName) {
            "alpha" -> DynamicAnimation.ALPHA
            "translationX" -> DynamicAnimation.TRANSLATION_X
            "translationY" -> DynamicAnimation.TRANSLATION_Y
            "scaleX" -> DynamicAnimation.SCALE_X
            "scaleY" -> DynamicAnimation.SCALE_Y
            "rotation" -> DynamicAnimation.ROTATION
            else -> return
        }
        runningSpringAnimations[viewProperty]?.let { spring ->
            if (spring.isRunning) {
                spring.cancel()
            }
        }
        runningSpringAnimations.remove(viewProperty)
    }

    fun cleanup() {
        for (animator in runningAnimators.values) {
            animator.cancel()
        }
        runningAnimators.clear()

        for (spring in runningSpringAnimations.values) {
            if (spring.isRunning) {
                spring.cancel()
            }
        }
        runningSpringAnimations.clear()

        prevOpacity = null
        prevTranslateX = null
        prevTranslateY = null
        prevScale = null
        prevRotate = null

        isFirstMount = true
    }
}
