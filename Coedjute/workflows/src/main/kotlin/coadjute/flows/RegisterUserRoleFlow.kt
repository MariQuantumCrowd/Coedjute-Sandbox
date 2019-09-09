package coadjute.flows

import coadjute.contracts.UserOrganizationRoleContract
import coadjute.functions.BroadcastFlow
import coadjute.functions.FlowFunctions
import coadjute.states.UserOrganizationRoleState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class RegisterUserRoleFlow (private val name: String): FlowFunctions()
{
    override fun call(): SignedTransaction
    {
        print("                                                  \n")
        print("**************************************************\n")
        print("*    STARTING FLOW - REGISTER USER ORGANIZATION FLOW    *\n")
        print("**************************************************\n")
        print("                                                  \n")

        val stx = verifyAndSign(transaction())
        return subFlow(FinalityFlow(stx, listOf())).also {
            subFlow(BroadcastFlow(it))
        }
    }

    private fun outState(): UserOrganizationRoleState
    {
        return UserOrganizationRoleState(
                id = UniqueIdentifier(),
                name = name,
                linearId = UniqueIdentifier(),
                participants = listOf(ourIdentity)
        )
    }

    private fun transaction(): TransactionBuilder
    {
        val cmd = Command(UserOrganizationRoleContract.Commands.Role(), ourIdentity.owningKey)
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val builder = TransactionBuilder(notary = notary)
        builder.addOutputState(outState(), UserOrganizationRoleContract.USERORG_ID)
        builder.addCommand(cmd)
        return builder
    }
}