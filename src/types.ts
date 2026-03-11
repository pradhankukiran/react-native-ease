export type EasingType = 'linear' | 'easeIn' | 'easeOut' | 'easeInOut';

export type TimingTransition = {
  type: 'timing';
  duration?: number;
  easing?: EasingType;
};

export type SpringTransition = {
  type: 'spring';
  damping?: number;
  stiffness?: number;
  mass?: number;
};

export type Transition = TimingTransition | SpringTransition;

export type AnimateProps = {
  opacity?: number;
  translateX?: number;
  translateY?: number;
  scale?: number;
  rotate?: number;
};
