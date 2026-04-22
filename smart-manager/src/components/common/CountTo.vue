<template>
    <span>{{ output }}</span>
</template>

<script setup lang="ts">
import { computed, toRef } from 'vue'
import { useCountUp } from '../../composables/useCountUp'

const props = defineProps({
    startVal: {
        type: Number,
        default: 0
    },
    endVal: {
        type: [Number, String],
        required: true
    },
    duration: {
        type: Number,
        default: 2000
    },
    decimals: {
        type: Number,
        default: 0
    },
    prefix: {
        type: String,
        default: ''
    },
    suffix: {
        type: String,
        default: ''
    }
})

const endValRef = toRef(props, 'endVal')
const { currentVal } = useCountUp(endValRef, {
    startVal: props.startVal,
    duration: props.duration,
    decimalPlaces: props.decimals
})

const output = computed(() => {
    return props.prefix + currentVal.value + props.suffix
})
</script>
