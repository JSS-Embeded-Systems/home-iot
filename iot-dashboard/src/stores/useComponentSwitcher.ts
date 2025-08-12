import { shallowRef } from 'vue';
import type { Component, ShallowRef } from 'vue';

export function useComponentSwitcher() {
  // Allow dynamic component keys; preseed with known ones
  const components: Record<string, ShallowRef<Component | undefined>> = {
    home: shallowRef<Component>(),
    dashboard: shallowRef<Component>(),
    settings: shallowRef<Component>()
  };

  // Current active component
  const currentComponent = shallowRef<Component>();

    // Initialize with provided component map (dynamic keys supported)
    const init = (componentMap: Record<string, Component>) => {
    for (const [key, comp] of Object.entries(componentMap)) {
      components[key] = shallowRef<Component | undefined>(comp);
    }

    const firstKey = Object.keys(componentMap)[0] ?? Object.keys(components)[0];
    if (firstKey && components[firstKey]?.value) {
      currentComponent.value = components[firstKey].value;
    }
  };

  // Switch to a specific component
  const switchTo = (name: string) => {
    const ref = components[name];
    if (ref?.value) {
      currentComponent.value = ref.value;
    }
  };

  return {
    currentComponent,
    switchTo,
    init
  };
}
