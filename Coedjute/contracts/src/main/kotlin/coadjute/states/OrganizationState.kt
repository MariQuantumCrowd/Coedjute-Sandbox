package coadjute.states

import coadjute.contracts.OrganizationContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

// *********
// * State *
// *********
@BelongsToContract(OrganizationContract::class)
data class OrganizationState (val organizationName: String,
                              val registerDate: String,
                              val user: List<UserState>?,
                              override val linearId: UniqueIdentifier,
                              override val participants: List<Party>): LinearState
