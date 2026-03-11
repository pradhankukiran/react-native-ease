# 🍃 react-native-ease

Lightweight declarative animations powered by platform APIs. Uses Core Animation on iOS and Animator on Android — zero JS overhead.

## Goals

- **Fast** — Animations run entirely on native platform APIs (CAAnimation, ObjectAnimator/SpringAnimation). No JS animation loop, no worklets, no shared values.
- **Simple** — CSS-transition-like API. Set target values, get smooth animations. One component, a few props.
- **Lightweight** — Minimal native code, no C++ runtime, no custom animation engine. Just a thin declarative wrapper around what the OS already provides.
- **Interruptible** — Changing values mid-animation smoothly redirects to the new target. No jumps.

## Non-Goals

- **Complex gesture-driven animations** — If you need pan/pinch-driven animations, animation worklets, or shared values across components, use [react-native-reanimated](https://github.com/software-mansion/react-native-reanimated).
- **Layout animations** — Animating width/height/layout changes is not supported.
- **Shared element transitions** — Use Reanimated or React Navigation's shared element transitions.
- **Old architecture** — Fabric (new architecture) only.

## When to use this vs Reanimated

| Use react-native-ease | Use Reanimated |
|---|---|
| Fade in a view | Gesture-driven animations |
| Slide/translate on state change | Complex interpolations |
| Scale/rotate on press | Shared values across components |
| Simple enter animations | Layout animations |
| You want zero config | You need animation worklets |

## Installation

```bash
npm install react-native-ease
# or
yarn add react-native-ease
```

## Quick Start

```tsx
import { EaseView } from 'react-native-ease';

function FadeCard({ visible, children }) {
  return (
    <EaseView
      animate={{ opacity: visible ? 1 : 0 }}
      transition={{ type: 'timing', duration: 300 }}
      style={styles.card}
    >
      {children}
    </EaseView>
  );
}
```

`EaseView` works like a regular `View` — it accepts children, styles, and all standard view props. When values in `animate` change, it smoothly transitions to the new values using native platform animations.

## Guide

### Timing Animations

Timing animations transition from one value to another over a fixed duration with an easing curve.

```tsx
<EaseView
  animate={{ opacity: isVisible ? 1 : 0 }}
  transition={{ type: 'timing', duration: 300, easing: 'easeOut' }}
/>
```

| Parameter | Type | Default | Description |
|---|---|---|---|
| `duration` | `number` | `300` | Duration in milliseconds |
| `easing` | `string` | `'easeInOut'` | Easing curve |

Available easing curves:

- `'linear'` — constant speed
- `'easeIn'` — starts slow, accelerates
- `'easeOut'` — starts fast, decelerates
- `'easeInOut'` — slow start and end, fast middle

### Spring Animations

Spring animations use a physics-based model for natural-feeling motion. Great for interactive elements.

```tsx
<EaseView
  animate={{ translateX: isOpen ? 200 : 0 }}
  transition={{ type: 'spring', damping: 15, stiffness: 120, mass: 1 }}
/>
```

| Parameter | Type | Default | Description |
|---|---|---|---|
| `damping` | `number` | `15` | Friction — higher values reduce oscillation |
| `stiffness` | `number` | `120` | Spring constant — higher values mean faster animation |
| `mass` | `number` | `1` | Mass of the object — higher values mean slower, more momentum |

Spring presets for common feels:

```tsx
// Snappy (no bounce)
{ type: 'spring', damping: 20, stiffness: 300, mass: 1 }

// Gentle bounce
{ type: 'spring', damping: 12, stiffness: 120, mass: 1 }

// Bouncy
{ type: 'spring', damping: 8, stiffness: 200, mass: 1 }

// Slow and heavy
{ type: 'spring', damping: 20, stiffness: 60, mass: 2 }
```

### Animatable Properties

All properties are set in the `animate` prop as flat values (no transform array).

```tsx
<EaseView
  animate={{
    opacity: 1,       // 0 to 1
    translateX: 0,    // pixels
    translateY: 0,    // pixels
    scale: 1,         // 1 = normal size
    rotate: 0,        // degrees
  }}
/>
```

You can animate any combination of properties simultaneously. All properties share the same transition config.

### Enter Animations

Use `initialAnimate` to set starting values. On mount, the view starts at `initialAnimate` values and animates to `animate` values.

```tsx
// Fade in and slide up on mount
<EaseView
  initialAnimate={{ opacity: 0, translateY: 20 }}
  animate={{ opacity: 1, translateY: 0 }}
  transition={{ type: 'spring', damping: 15, stiffness: 120, mass: 1 }}
/>
```

Without `initialAnimate`, the view renders at the `animate` values immediately with no animation on mount.

### Interruption

Animations are interruptible by default. If you change `animate` values while an animation is running, it smoothly redirects to the new target from wherever it currently is — no jumping or restarting.

```tsx
// Rapidly toggling this is fine — each toggle smoothly
// redirects the animation from its current position
<EaseView
  animate={{ translateX: isLeft ? 0 : 200 }}
  transition={{ type: 'spring', damping: 15, stiffness: 120, mass: 1 }}
/>
```

### Style Handling

Use `animate` for animated properties and `style` for everything else. If you accidentally put `opacity` or `transform` in `style`, they will be ignored and you'll get a dev warning.

```tsx
// ✅ Correct
<EaseView
  animate={{ opacity: 1, translateY: 0 }}
  style={{ backgroundColor: 'white', borderRadius: 12, padding: 16 }}
/>

// ⚠️ opacity in style will be ignored with a warning
<EaseView
  animate={{ translateY: 0 }}
  style={{ opacity: 0.5, backgroundColor: 'white' }}
/>
```

## API Reference

### `<EaseView>`

A `View` that animates property changes using native platform APIs.

| Prop | Type | Description |
|---|---|---|
| `animate` | `AnimateProps` | Target values for animated properties |
| `initialAnimate` | `AnimateProps` | Starting values for enter animations (animates to `animate` on mount) |
| `transition` | `Transition` | Animation configuration (timing or spring) |
| `style` | `ViewStyle` | Non-animated styles (layout, colors, borders, etc.) |
| `children` | `ReactNode` | Child elements |
| ...rest | `ViewProps` | All other standard View props |

### `AnimateProps`

| Property | Type | Default | Description |
|---|---|---|---|
| `opacity` | `number` | `1` | View opacity (0–1) |
| `translateX` | `number` | `0` | Horizontal translation in pixels |
| `translateY` | `number` | `0` | Vertical translation in pixels |
| `scale` | `number` | `1` | Uniform scale factor |
| `rotate` | `number` | `0` | Rotation in degrees |

Properties not specified in `animate` default to their identity values.

### `TimingTransition`

```tsx
{
  type: 'timing';
  duration?: number;  // default: 300 (ms)
  easing?: 'linear' | 'easeIn' | 'easeOut' | 'easeInOut';  // default: 'easeInOut'
}
```

### `SpringTransition`

```tsx
{
  type: 'spring';
  damping?: number;    // default: 15
  stiffness?: number;  // default: 120
  mass?: number;       // default: 1
}
```

## How It Works

`EaseView` is a native Fabric component. The JS side flattens your `animate` and `transition` props into flat native props. When those props change, the native view:

1. **Diffs** previous vs new values to find what changed
2. **Reads** the current in-flight value (for smooth interruption)
3. **Creates** a platform-native animation from the current value to the new target
4. **Sets** the final value immediately on the model layer

On iOS, this uses `CABasicAnimation`/`CASpringAnimation` on `CALayer` key paths. On Android, this uses `ObjectAnimator`/`SpringAnimation` on `View` properties. No JS thread involvement during the animation.

## Requirements

- React Native 0.76+ (new architecture / Fabric)
- iOS 15.1+
- Android minSdk 24+

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
