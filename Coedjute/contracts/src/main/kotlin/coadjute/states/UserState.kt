package coadjute.states

import coadjute.contracts.UserContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import net.corda.core.serialization.CordaSerializable

@BelongsToContract(UserContract::class)
data class UserState (val name: String,
                      val emailAddress: String,
                      val role: String,
                      val phoneNumber: String,
                      val country: String,
                      val howDidYouHearAboutUs: String,
                      val howCanWeHelp: String,
                      val organizationId: UniqueIdentifier,
                      override val linearId: UniqueIdentifier,
                      override val participants: List<Party>): LinearState
// Comapny A