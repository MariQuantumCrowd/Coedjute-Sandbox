package coadjute.states

import coadjute.contracts.UserOrganizationRoleContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(UserOrganizationRoleContract::class)
data class UserOrganizationRoleState(val id: UniqueIdentifier,
                                     val name: String, // ADMINISTRATOR, USER
                                     override val linearId: UniqueIdentifier,
                                     override val participants: List<Party>): LinearState