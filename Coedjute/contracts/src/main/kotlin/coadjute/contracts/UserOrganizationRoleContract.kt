package coadjute.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction

class UserOrganizationRoleContract : Contract
{
    companion object
    {
        const val USERORG_ID = "coadjute.contracts.UserOrganizationRoleContract"
    }

    override fun verify(tx: LedgerTransaction)
    {

    }

    interface Commands : CommandData
    {
        class Role : TypeOnlyCommandData(), Commands
    }
}
