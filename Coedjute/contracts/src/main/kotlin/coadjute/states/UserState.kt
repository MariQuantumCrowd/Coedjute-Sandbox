package coadjute.states

import coadjute.contracts.UserContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party

@BelongsToContract(UserContract::class)
data class UserState (val name: String,
                      val emailAddress: String,
                      val role: String,
                      val phoneNumber: String,
                      val country: String,
                      val howDidYouHearAboutUs: String,
                      val howCanWeHelp: String,
                      override val linearId: UniqueIdentifier,
                      override val participants: List<Party>): LinearState