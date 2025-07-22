import { defineComponent, computed } from 'vue';
import { useStateStore } from '@/stores/viewStore';
import type { StateName } from '@/types/state';

export default defineComponent({
  name: 'StateSwitcher',

  setup() {
    const store = useStateStore();

    const currentState = computed(() => store.getCurrentState);
    const availableStates = computed(() => store.availableStates);

    const setState = (stateName: StateName) => {
      store.setState(stateName);
    };

    return {
      currentState,
      availableStates,
      setState
    };
  }
});
