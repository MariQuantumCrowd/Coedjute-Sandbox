package coadjuteFlow.functions

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction

@InitiatingFlow
class BroadcastFlow(private val stx: SignedTransaction) : FlowFunctionsUnit()
{
    @Suspendable
    override fun call()
    {
        val session = initiateFlow(RegulatorNode())
        subFlow(SendTransactionFlow(session, stx))
    }
}

@InitiatedBy(BroadcastFlow::class)
class BroadcastFlowResponder(private val session: FlowSession) : FlowLogic<Unit>()
{
    @Suspendable
    override fun call()
    {
        subFlow(ReceiveTransactionFlow(session, statesToRecord = StatesToRecord.ALL_VISIBLE))
    }
}
