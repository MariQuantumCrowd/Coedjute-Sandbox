package coadjute.states

import coadjute.contracts.TemplateContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

// *********
// * State *
// *********
@BelongsToContract(TemplateContract::class)
data class OrganizationState (val organizationName: String,
                              val registerDate: String,
                              val userList: List<UserListState>?,
                              override val linearId: UniqueIdentifier,
                              override val participants: List<Party>): LinearState
