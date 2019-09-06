package coadjute.flows

import coadjute.functions.*
import coadjute.states.*
import co.paralleluniverse.fibers.Suspendable
import coadjute.contracts.OrganizationContract
import coadjute.contracts.OrganizationContract.Companion.ORG_ID
import coadjute.contracts.UserContract
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


@InitiatingFlow
@StartableByRPC
class RegisterUserFlow(private val Name: String,
                       private val Email: String,
                       private val Role: String,
                       private val PhoneNumber: String,
                       private val Country: String,
                       private val howDidYouHearAboutUs: String,
                       private val howCanWeHelp: String,
                       private val organizationId: String):FlowFunctions(){
    @Suspendable
    override fun call(): SignedTransaction {
        print("                                                  \n")
        print("**************************************************\n")
        print("*    STARTING FLOW - REGISTER USER FLOW    *\n")
        print("**************************************************\n")
        print("                                                  \n")

        progressTracker.currentStep = INITIALIZING
        progressTracker.currentStep = BUILDING
        val transaction =  transaction()

        progressTracker.currentStep = SIGNING
        val signedTransaction = verifyAndSign(transaction)
        val session = (outputState().participants - ourIdentity).map { initiateFlow(it) }

        progressTracker.currentStep = COLLECTING
        val transactionSignedByAllParties = collectSignature(signedTransaction, session)

        progressTracker.currentStep = FINALIZING
        return subFlow(FinalityFlow(transactionSignedByAllParties, session)).also {
            subFlow(BroadcastFlow(it))
        }
    }


    private fun outputState(): UserState
    {
        return UserState(
             name = Name,
             emailAddress = Email,
             role =  Role,
             phoneNumber = PhoneNumber,
             country = Country,
             howDidYouHearAboutUs = howDidYouHearAboutUs,
             howCanWeHelp = howCanWeHelp,
             organizationId = UniqueIdentifier.fromString(organizationId),
             linearId = UniqueIdentifier(),
             participants = listOf(ourIdentity)
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val registerCommand = Command(UserContract.Commands.Register(), outputState().participants.map { it.owningKey })
        val builder = TransactionBuilder(notary)
        builder.addOutputState(outputState(), ORG_ID)
        builder.addCommand(registerCommand)
        return builder
    }

}

@InitiatedBy(RegisterUserFlow::class)
class RegisterUserFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
            }
        }
        val signedTransaction = subFlow(signTransactionFlow)
        return subFlow(ReceiveFinalityFlow(otherSideSession = flowSession, expectedTxId = signedTransaction.id))
    }
}