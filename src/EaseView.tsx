import { StyleSheet, type ViewProps } from 'react-native';
import NativeEaseView from './EaseViewNativeComponent';
import type { AnimateProps, Transition } from './types';

const IDENTITY: Required<AnimateProps> = {
  opacity: 1,
  translateX: 0,
  translateY: 0,
  scale: 1,
  rotate: 0,
};

export type EaseViewProps = ViewProps & {
  animate?: AnimateProps;
  initialAnimate?: AnimateProps;
  transition?: Transition;
};

export function EaseView({
  animate,
  initialAnimate,
  transition,
  style,
  ...rest
}: EaseViewProps) {
  const resolved = { ...IDENTITY, ...animate };
  const resolvedInitial = { ...IDENTITY, ...(initialAnimate ?? animate) };

  // Strip animated properties from style
  let cleanStyle = style;
  if (style) {
    const flat = StyleSheet.flatten(style);
    if (flat) {
      const { opacity, transform, ...remaining } = flat;
      if (__DEV__ && (opacity !== undefined || transform !== undefined)) {
        console.warn(
          'react-native-ease: Set opacity/transforms in the animate prop, not style. ' +
            'Animated properties in style will be ignored.'
        );
      }
      cleanStyle = remaining;
    }
  }

  // Resolve transition config
  const transitionType = transition?.type ?? 'timing';
  const transitionDuration =
    transition?.type === 'timing' ? (transition.duration ?? 300) : 300;
  const transitionEasing =
    transition?.type === 'timing'
      ? (transition.easing ?? 'easeInOut')
      : 'easeInOut';
  const transitionDamping =
    transition?.type === 'spring' ? (transition.damping ?? 15) : 15;
  const transitionStiffness =
    transition?.type === 'spring' ? (transition.stiffness ?? 120) : 120;
  const transitionMass =
    transition?.type === 'spring' ? (transition.mass ?? 1) : 1;

  return (
    <NativeEaseView
      style={cleanStyle}
      animateOpacity={resolved.opacity}
      animateTranslateX={resolved.translateX}
      animateTranslateY={resolved.translateY}
      animateScale={resolved.scale}
      animateRotate={resolved.rotate}
      initialAnimateOpacity={resolvedInitial.opacity}
      initialAnimateTranslateX={resolvedInitial.translateX}
      initialAnimateTranslateY={resolvedInitial.translateY}
      initialAnimateScale={resolvedInitial.scale}
      initialAnimateRotate={resolvedInitial.rotate}
      transitionType={transitionType}
      transitionDuration={transitionDuration}
      transitionEasing={transitionEasing}
      transitionDamping={transitionDamping}
      transitionStiffness={transitionStiffness}
      transitionMass={transitionMass}
      {...rest}
    />
  );
}
