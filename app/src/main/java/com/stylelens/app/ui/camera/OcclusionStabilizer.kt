package com.stylelens.app.ui.camera

class OcclusionStabilizer(
    private val framesToHide: Int = 2,
    private val framesToShow: Int = 4
) {

    private var occludedFrames = 0
    private var clearFrames = 0

    private var stableOccluded = false

    fun update(
        currentlyOccluded: Boolean
    ): Boolean {

        if (currentlyOccluded) {

            occludedFrames++
            clearFrames = 0

            if (occludedFrames >= framesToHide) {
                stableOccluded = true
            }

        } else {

            clearFrames++
            occludedFrames = 0

            if (clearFrames >= framesToShow) {
                stableOccluded = false
            }
        }

        return stableOccluded
    }

    fun reset() {

        occludedFrames = 0
        clearFrames = 0
        stableOccluded = false
    }
}