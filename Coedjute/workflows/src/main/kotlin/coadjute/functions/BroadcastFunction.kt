package coadjute.functions

import net.corda.core.flows.FlowLogic
import net.corda.core.identity.Party
import net.corda.core.utilities.ProgressTracker

abstract class FlowFunctionsUnit : FlowLogic<Unit>() {

    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)

    fun RegulatorNode(): Party {
        return serviceHub.identityService.partiesFromName("Regulator", false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for Regulator")
    }
}
