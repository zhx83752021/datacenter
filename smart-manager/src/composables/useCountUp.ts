import { ref, onMounted, type Ref } from 'vue'

interface CountUpOptions {
    startVal?: number
    duration?: number
    decimalPlaces?: number
}

export function useCountUp(targetVal: Ref<number | string>, options: CountUpOptions = {}) {
    const currentVal = ref(options.startVal || 0)
    const { duration = 2000, decimalPlaces = 0 } = options

    const animate = () => {
        const start = options.startVal || 0
        const end = Number(String(targetVal.value).replace(/,/g, ''))
        const startTime = performance.now()

        const step = (currentTime: number) => {
            const elapsed = currentTime - startTime
            const progress = Math.min(elapsed / duration, 1)

            // Ease out quart
            const ease = 1 - Math.pow(1 - progress, 4)

            const val = start + (end - start) * ease
            currentVal.value = Number(val.toFixed(decimalPlaces))

            if (progress < 1) {
                requestAnimationFrame(step)
            } else {
                currentVal.value = end
            }
        }

        requestAnimationFrame(step)
    }

    onMounted(() => {
        animate()
    })

    return {
        currentVal
    }
}
