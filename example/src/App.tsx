import React, { useState } from 'react';
import {
  View,
  StyleSheet,
  Text,
  Pressable,
  SafeAreaView,
  ScrollView,
} from 'react-native';
import { EaseView } from 'react-native-ease';

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );
}

function FadeDemo() {
  const [visible, setVisible] = useState(true);
  return (
    <Section title="Fade">
      <EaseView
        animate={{ opacity: visible ? 1 : 0 }}
        transition={{ type: 'timing', duration: 300, easing: 'easeOut' }}
        style={styles.box}
      />
      <Pressable style={styles.button} onPress={() => setVisible((v) => !v)}>
        <Text style={styles.buttonText}>{visible ? 'Hide' : 'Show'}</Text>
      </Pressable>
    </Section>
  );
}

function SlideDemo() {
  const [moved, setMoved] = useState(false);
  return (
    <Section title="Slide (Spring)">
      <EaseView
        animate={{ translateX: moved ? 150 : 0 }}
        transition={{ type: 'spring', damping: 15, stiffness: 120, mass: 1 }}
        style={styles.box}
      />
      <Pressable style={styles.button} onPress={() => setMoved((v) => !v)}>
        <Text style={styles.buttonText}>{moved ? 'Back' : 'Slide'}</Text>
      </Pressable>
    </Section>
  );
}

function EnterDemo() {
  const [key, setKey] = useState(0);
  return (
    <Section title="Enter Animation">
      <EaseView
        key={key}
        initialAnimate={{ opacity: 0, translateY: 30, scale: 0.8 }}
        animate={{ opacity: 1, translateY: 0, scale: 1 }}
        transition={{ type: 'spring', damping: 12, stiffness: 120, mass: 1 }}
        style={styles.box}
      />
      <Pressable style={styles.button} onPress={() => setKey((k) => k + 1)}>
        <Text style={styles.buttonText}>Replay</Text>
      </Pressable>
    </Section>
  );
}

function RotateDemo() {
  const [rotated, setRotated] = useState(false);
  return (
    <Section title="Rotate">
      <EaseView
        animate={{ rotate: rotated ? 180 : 0 }}
        transition={{ type: 'spring', damping: 15, stiffness: 120, mass: 1 }}
        style={styles.box}
      >
        <Text style={styles.boxText}>🍃</Text>
      </EaseView>
      <Pressable style={styles.button} onPress={() => setRotated((v) => !v)}>
        <Text style={styles.buttonText}>Rotate</Text>
      </Pressable>
    </Section>
  );
}

function CombinedDemo() {
  const [active, setActive] = useState(false);
  return (
    <Section title="Combined">
      <EaseView
        animate={{
          opacity: active ? 1 : 0.3,
          scale: active ? 1.2 : 1,
          translateY: active ? -20 : 0,
          rotate: active ? 15 : 0,
        }}
        transition={{ type: 'spring', damping: 12, stiffness: 200, mass: 1 }}
        style={styles.box}
      />
      <Pressable style={styles.button} onPress={() => setActive((v) => !v)}>
        <Text style={styles.buttonText}>{active ? 'Reset' : 'Animate'}</Text>
      </Pressable>
    </Section>
  );
}

export default function App() {
  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <Text style={styles.title}>🍃 react-native-ease</Text>
        <FadeDemo />
        <SlideDemo />
        <EnterDemo />
        <RotateDemo />
        <CombinedDemo />
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  scrollContent: {
    padding: 20,
  },
  title: {
    fontSize: 28,
    fontWeight: 'bold',
    marginBottom: 24,
    textAlign: 'center',
  },
  section: {
    marginBottom: 32,
    alignItems: 'flex-start',
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 12,
  },
  box: {
    width: 80,
    height: 80,
    backgroundColor: '#4a90d9',
    borderRadius: 12,
    alignItems: 'center',
    justifyContent: 'center',
  },
  boxText: {
    fontSize: 32,
  },
  button: {
    marginTop: 12,
    paddingHorizontal: 20,
    paddingVertical: 10,
    backgroundColor: '#333',
    borderRadius: 8,
  },
  buttonText: {
    color: '#fff',
    fontWeight: '600',
  },
});
