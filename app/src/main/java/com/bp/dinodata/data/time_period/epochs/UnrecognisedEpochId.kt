package com.bp.dinodata.data.time_period.epochs

class UnrecognisedEpochId(id: IEpochId): Exception(
    "Unfamiliar epoch id $id."
)