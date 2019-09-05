package coadjute.flows

import coadjute.functions.*
import coadjute.states.*
import co.paralleluniverse.fibers.Suspendable
import coadjute.contracts.OrganizationContract
import coadjute.contracts.OrganizationContract.Companion.ORG_ID
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant

@StartableByRPC
class RegisterOrganizationFlow(private val organizationName: String): FlowFunctions()
{
    @Suspendable
    override fun call(): SignedTransaction {
        print("                                                  \n")
        print("**************************************************\n")
        print("*    STARTING FLOW - REGISTER ORGANIZATION FLOW    *\n")
        print("**************************************************\n")
        print("                                                  \n")

        progressTracker.currentStep = INITIALIZING
        progressTracker.currentStep = BUILDING
        val transaction =  transaction()

        progressTracker.currentStep = SIGNING
        val signedTransaction = verifyAndSign(transaction)

        progressTracker.currentStep = FINALIZING
        return subFlow(FinalityFlow(signedTransaction, listOf())).also {
            subFlow(BroadcastFlow(it))
        }
    }

    private fun outputState(): OrganizationState {

        return OrganizationState(
                organizationName = organizationName,
                registerDate = Instant.now().toString(),
                user = null,
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity)
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val registerCommand = Command(OrganizationContract.Commands.Register(), outputState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), ORG_ID)
        builder.addCommand(registerCommand)
        return builder
    }

}