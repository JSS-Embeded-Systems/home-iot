import { shallowRef } from 'vue';
import type { Component } from 'vue';

export function useComponentSwitcher() {
  // Store components with shallowRef to prevent unnecessary reactivity
  const components = {
    home: shallowRef<Component>(),
    dashboard: shallowRef<Component>(),
    settings: shallowRef<Component>()
  };

  // Current active component
  const currentComponent = shallowRef<Component>();

  // Initialize with first component
  const init = (componentMap: Record<string, Component>) => {
    Object.assign(components, Object.fromEntries(
      Object.entries(componentMap).map(([key, comp]) => [key, shallowRef(comp)])
    ));
    currentComponent.value = Object.values(components)[0].value;
  };

  // Switch to a specific component
  const switchTo = (name: string) => {
    if (components[name]) {
      currentComponent.value = components[name].value;
    }
  };

  return {
    currentComponent,
    switchTo,
    init
  };
}
