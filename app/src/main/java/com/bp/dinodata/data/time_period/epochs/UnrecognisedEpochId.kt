package com.bp.dinodata.data.time_period.epochs

import com.bp.dinodata.data.time_period.IEpochId

class UnrecognisedEpochId(id: IEpochId): Exception(
    "Unfamiliar epoch id $id."
)