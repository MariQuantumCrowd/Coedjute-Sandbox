package coadjuteFlow.functions

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.math.BigInteger
import java.security.MessageDigest

abstract class FlowFunctions : FlowLogic<SignedTransaction>()
{
    override val progressTracker = ProgressTracker(INITIALIZING, BUILDING, SIGNING, COLLECTING, FINALIZING)

    fun verifyAndSign(transaction: TransactionBuilder): SignedTransaction
    {
        progressTracker.currentStep = SIGNING
        transaction.verify(serviceHub)
        return serviceHub.signInitialTransaction(transaction)
    }

    @Suspendable
    fun collectSignature(
            transaction: SignedTransaction,
            sessions: List<FlowSession>
    ): SignedTransaction = subFlow(CollectSignaturesFlow(transaction, sessions))

    @Suspendable
    fun recordTransactionWithOtherParty(transaction: SignedTransaction, sessions: List<FlowSession>): SignedTransaction
    {
        progressTracker.currentStep = FINALIZING
        return subFlow(FinalityFlow(transaction, sessions))
    }

    @Suspendable
    fun recordTransactionWithoutOtherParty(transaction: SignedTransaction) : SignedTransaction
    {
        progressTracker.currentStep = FINALIZING
        return subFlow(FinalityFlow(transaction, emptyList()))
    }

    fun Client(): Party
    {
        return serviceHub.identityService.partiesFromName("Client", false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for Regulator")
    }

    fun PrincipalDesigner(): Party
    {
        return serviceHub.identityService.partiesFromName("Principal Designer", false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for Regulator")
    }

    fun PrincipalConstructor(): Party
    {
        return serviceHub.identityService.partiesFromName("Principal Constructor", false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for Regulator")
    }

    fun RegulatorNode(): Party
    {
        return serviceHub.identityService.partiesFromName("Regulator", false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for Regulator")
    }
    fun stringToParty(name: String): Party
    {
        return serviceHub.identityService.partiesFromName(name, false).singleOrNull()
                ?: throw IllegalArgumentException("No match found for $name")
    }

    fun stringToUniqueIdentifier(id: String): UniqueIdentifier
    {
        return UniqueIdentifier.fromString(id)
    }

//    fun inputStateRefProductState(id: String): StateAndRef<ProductState> {
//        val linearId = stringToUniqueIdentifier(id)
//        val criteria = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(linearId))
//        return serviceHub.vaultService.queryBy<ProductState>(criteria = criteria).states.single()
//    }
//
//    fun checkPartNumberAndSerialNumber(partNumber: String, serialNumber: String): StateAndRef<ProductState>? {
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<ProductState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.partNumber == partNumber &&
//                stateAndRef.state.data.serialNumber == serialNumber
//        }
//    }
//
//    fun checkSerialNumberNotRegistered(serialNumber: String): StateAndRef<ProductState>? {
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<ProductState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.serialNumber == serialNumber
//        }
//    }
//
//    fun checkSerialNumberForConnectionStatus(serialNumber: String): StateAndRef<SellProductState>? {
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<SellProductState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.serialNumber == serialNumber
//        }
//    }
//
//    fun inputStateCompanyName(companyName: String): StateAndRef<CompanyState>? {
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<CompanyState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.companyName == companyName
//        }
//    }
////    fun inputCompanyName(companyName: String): StateAndRef<SellProductState>? {
////        val criteria = QueryCriteria.VaultQueryCriteria()
////        return serviceHub.vaultService.queryBy<SellProductState>(criteria).states.find {
////            stateAndRef ->  stateAndRef.state.data.buyers == companyName
////        }
////    }
//    fun inputStateSerialNumber(serialNumber: String): StateAndRef<SellProductState> {
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<SellProductState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.serialNumber == serialNumber
//        }?: throw IllegalArgumentException("serial number not found")
//    }
//
//    fun inputStateMaintenanceRequestContractNumber(number: Long): StateAndRef<MaintenanceContractState>{
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<MaintenanceContractState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.contractNumber == number
//        }?: throw IllegalArgumentException("contract number not found")
//    }


//    fun checkSerialNumber(serialNumber: String): StateAndRef<ProductState>{
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<ProductState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.serialNumber == serialNumber
//        }?: throw IllegalArgumentException("serial number not found")
//    }
//
//    fun checkDocumentName(name: String): StateAndRef<DocumentState>{
//        val criteria = QueryCriteria.VaultQueryCriteria()
//        return serviceHub.vaultService.queryBy<DocumentState>(criteria).states.find {
//            stateAndRef ->  stateAndRef.state.data.name != name
//        }?: throw IllegalArgumentException("name is already exist")
//    }

    fun String.sha256(): String {
        val md = MessageDigest.getInstance("SHA-256")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(64, '0')
    }

//    fun requirements(contractNumber: Long): Requirements
//    {
//        val input = inputStateMaintenanceRequestContractNumber(contractNumber).state.data.requirements!!.single()
//        return Requirements(input.maintenanceRequest,input.buybackInspectorJobCard,input.inspectorJobCard,
//                input.invoice,input.mechanicJobCard,input.serviceableTag,input.shippingDetail,input.workPackage,input.repairQuote,
//                input.salesQuote)
//    }
}