/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.tpch;

import com.facebook.presto.spi.HostAddress;
import com.facebook.presto.spi.PartitionKey;
import com.facebook.presto.spi.PartitionedSplit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

// Right now, splits are just the entire TPCH table
public class TpchSplit
        implements PartitionedSplit
{
    private final TpchTableHandle tableHandle;
    private final int totalParts;
    private final int partNumber;

    private final String partition;
    private final List<HostAddress> addresses;

    @JsonCreator
    public TpchSplit(@JsonProperty("tableHandle") TpchTableHandle tableHandle,
            @JsonProperty("partNumber") int partNumber,
            @JsonProperty("totalParts") int totalParts,
            @JsonProperty("addresses") List<HostAddress> addresses)
    {
        checkState(partNumber >= 0, "partNumber must be >= 0");
        checkState(totalParts >= 1, "totalParts must be >= 1");
        checkState(totalParts > partNumber, "totalParts must be > partNumber");

        this.tableHandle = checkNotNull(tableHandle, "tableHandle is null");
        this.partNumber = partNumber;
        this.totalParts = totalParts;
        this.partition = "tpch_part_" + partNumber;
        this.addresses = ImmutableList.copyOf(checkNotNull(addresses, "addresses is null"));
    }

    @VisibleForTesting
    public TpchSplit(TpchTableHandle tableHandle)
    {
        this(tableHandle, 0, 1, ImmutableList.<HostAddress>of());
    }

    @JsonProperty
    public TpchTableHandle getTableHandle()
    {
        return tableHandle;
    }

    @JsonProperty
    public int getTotalParts()
    {
        return totalParts;
    }

    @JsonProperty
    public int getPartNumber()
    {
        return partNumber;
    }

    @Override
    public String getPartitionId()
    {
        return partition;
    }

    @Override
    public boolean isLastSplit()
    {
        return true;
    }

    @Override
    public List<PartitionKey> getPartitionKeys()
    {
        return ImmutableList.of();
    }

    @Override
    public Object getInfo()
    {
        return this;
    }

    @Override
    public boolean isRemotelyAccessible()
    {
        return false;
    }

    @JsonProperty
    @Override
    public List<HostAddress> getAddresses()
    {
        return addresses;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TpchSplit)) {
            return false;
        }

        TpchSplit tpchSplit = (TpchSplit) o;

        if (tableHandle.equals(tpchSplit.tableHandle)
                && partNumber == tpchSplit.partNumber
                && totalParts == tpchSplit.totalParts) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(tableHandle, partNumber, totalParts);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("tableHandle", tableHandle)
                .add("partNumber", partNumber)
                .add("totalParts", totalParts)
                .toString();
    }
}
