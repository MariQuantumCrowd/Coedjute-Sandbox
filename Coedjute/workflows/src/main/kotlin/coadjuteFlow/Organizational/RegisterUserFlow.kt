package coadjuteFlow.Organizational

import coadjuteFlow.functions.*
import coadjute.states.*
import co.paralleluniverse.fibers.Suspendable
import coadjute.contracts.TemplateContract
import coadjute.contracts.TemplateContract.Companion.ORG_ID
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import java.time.Instant


@InitiatingFlow
@StartableByRPC
class RegisterUserFlow(private val Name: String,
                       private val Email: String,
                       private val Role: String,
                       private val PhoneNumber: String,
                       private val Country: String,
                       private val howDidYouHearAboutUs: String,
                       private val howCanWeHelp: String):FlowFunctions(){
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


    private fun outputState(): UserListState {

        return UserListState(
                 name = Name,
                 emailAddress = Email,
                 role =  Role,
                 phoneNumber = PhoneNumber,
                 country = Country,
                 howDidYouHearAboutUs = howDidYouHearAboutUs,
                 howCanWeHelp = howCanWeHelp,
                 linearId = UniqueIdentifier(),
                 participants = listOf(ourIdentity)
        )
    }

    private fun transaction(): TransactionBuilder {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val registerCommand = Command(TemplateContract.Commands.Action(), outputState().participants.map { it.owningKey })
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