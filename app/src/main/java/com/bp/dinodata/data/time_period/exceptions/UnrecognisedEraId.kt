package com.bp.dinodata.data.time_period.exceptions

import com.bp.dinodata.data.time_period.era.IEraId

class UnrecognisedEraId(id: IEraId): Exception(
    "Unrecognised era id $id"
)