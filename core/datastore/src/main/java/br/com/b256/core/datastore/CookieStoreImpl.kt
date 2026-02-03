package br.com.b256.core.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class CookieStoreImpl @Inject constructor(
    private val dataStore: DataStore<StringListMapProto>,
) : CookieStore {

    override suspend fun all(): Map<String, List<String>> =
        dataStore.data.first().entriesMap.map { (key, value) ->
            key to value.valuesList.toList()
        }.toMap()

    override suspend fun save(
        key: String,
        value: List<String>,
    ) {
        dataStore.updateData { proto ->
            proto.toBuilder()
                .putEntries(
                    key,
                    StringListProto.newBuilder()
                        .addAllValues(value)
                        .build(),
                )
                .build()
        }
    }

    override suspend fun clear() {
        dataStore.updateData { StringListMapProto.getDefaultInstance() }
    }
}
