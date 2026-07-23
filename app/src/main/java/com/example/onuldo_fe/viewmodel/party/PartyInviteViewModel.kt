package com.example.onuldo_fe.viewmodel.party

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.onuldo_fe.model.party.PartyJoinError
import com.example.onuldo_fe.model.party.PartyJoinResult
import com.example.onuldo_fe.repository.party.PartyInviteRepository
import com.example.onuldo_fe.repository.party.PartyInviteRepositoryProvider
import com.example.onuldo_fe.ui.screen.party.InviteCodeError

data class PartyInviteUiState(
    val error: InviteCodeError? = null,
    val joinedPartyId: String? = null
)

class PartyInviteViewModel(
    private val repository: PartyInviteRepository = PartyInviteRepositoryProvider.provide()
) : ViewModel() {
    var uiState by mutableStateOf(PartyInviteUiState())
        private set

    fun joinParty(inviteCode: String) {
        uiState = when (val result = repository.joinParty(inviteCode)) {
            is PartyJoinResult.Success -> PartyInviteUiState(joinedPartyId = result.partyId)
            is PartyJoinResult.Failure -> PartyInviteUiState(error = result.error.toUiError())
        }
    }

    fun reset() {
        uiState = PartyInviteUiState()
    }
}

private fun PartyJoinError.toUiError() = when (this) {
    PartyJoinError.Invalid -> InviteCodeError.Invalid
    PartyJoinError.AlreadyStarted -> InviteCodeError.AlreadyStarted
    PartyJoinError.Full -> InviteCodeError.Full
    PartyJoinError.Expired -> InviteCodeError.Expired
}
