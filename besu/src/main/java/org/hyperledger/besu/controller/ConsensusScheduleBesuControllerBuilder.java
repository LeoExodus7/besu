/*
 * Copyright Hyperledger Besu Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 *  the License for the
 * specific language governing permissions and limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.besu.controller;

import org.hyperledger.besu.config.GenesisConfigFile;
import org.hyperledger.besu.consensus.qbft.pki.PkiBlockCreationConfiguration;
import org.hyperledger.besu.crypto.NodeKey;
import org.hyperledger.besu.datatypes.Hash;
import org.hyperledger.besu.ethereum.ConsensusContext;
import org.hyperledger.besu.ethereum.GasLimitCalculator;
import org.hyperledger.besu.ethereum.ProtocolContext;
import org.hyperledger.besu.ethereum.api.jsonrpc.methods.JsonRpcMethods;
import org.hyperledger.besu.ethereum.blockcreation.MiningCoordinator;
import org.hyperledger.besu.ethereum.chain.Blockchain;
import org.hyperledger.besu.ethereum.core.MiningParameters;
import org.hyperledger.besu.ethereum.core.PrivacyParameters;
import org.hyperledger.besu.ethereum.eth.EthProtocolConfiguration;
import org.hyperledger.besu.ethereum.eth.manager.EthContext;
import org.hyperledger.besu.ethereum.eth.manager.EthMessages;
import org.hyperledger.besu.ethereum.eth.manager.EthPeers;
import org.hyperledger.besu.ethereum.eth.manager.EthProtocolManager;
import org.hyperledger.besu.ethereum.eth.manager.EthScheduler;
import org.hyperledger.besu.ethereum.eth.peervalidation.PeerValidator;
import org.hyperledger.besu.ethereum.eth.sync.SynchronizerConfiguration;
import org.hyperledger.besu.ethereum.eth.sync.state.SyncState;
import org.hyperledger.besu.ethereum.eth.transactions.TransactionPool;
import org.hyperledger.besu.ethereum.eth.transactions.TransactionPoolConfiguration;
import org.hyperledger.besu.ethereum.mainnet.ProtocolSchedule;
import org.hyperledger.besu.ethereum.p2p.config.SubProtocolConfiguration;
import org.hyperledger.besu.ethereum.storage.StorageProvider;
import org.hyperledger.besu.ethereum.worldstate.DataStorageConfiguration;
import org.hyperledger.besu.ethereum.worldstate.PrunerConfiguration;
import org.hyperledger.besu.ethereum.worldstate.WorldStateArchive;
import org.hyperledger.besu.evm.internal.EvmConfiguration;
import org.hyperledger.besu.metrics.ObservableMetricsSystem;
import org.hyperledger.besu.plugin.services.permissioning.NodeMessagePermissioningProvider;

import java.math.BigInteger;
import java.nio.file.Path;
import java.time.Clock;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

/*
 This is a placeholder class for the QBFT migration logic. For now, all it does is to delegate any
 BesuControllerBuilder to the first controller in the list.
*/
public class ConsensusScheduleBesuControllerBuilder extends BesuControllerBuilder {

  private final Map<Long, BesuControllerBuilder> besuControllerBuilderSchedule = new HashMap<>();

  public ConsensusScheduleBesuControllerBuilder(
      final Map<Long, BesuControllerBuilder> besuControllerBuilderSchedule) {
    Preconditions.checkNotNull(
        besuControllerBuilderSchedule, "BesuControllerBuilder schedule can't be null");
    Preconditions.checkArgument(
        !besuControllerBuilderSchedule.isEmpty(), "BesuControllerBuilder schedule can't be empty");
    this.besuControllerBuilderSchedule.putAll(besuControllerBuilderSchedule);
  }

  @Override
  protected void prepForBuild() {
    besuControllerBuilderSchedule.get(0L).prepForBuild();
  }

  @Override
  protected MiningCoordinator createMiningCoordinator(
      final ProtocolSchedule protocolSchedule,
      final ProtocolContext protocolContext,
      final TransactionPool transactionPool,
      final MiningParameters miningParameters,
      final SyncState syncState,
      final EthProtocolManager ethProtocolManager) {
    return besuControllerBuilderSchedule
        .get(0L)
        .createMiningCoordinator(
            protocolSchedule,
            protocolContext,
            transactionPool,
            miningParameters,
            syncState,
            ethProtocolManager);
  }

  @Override
  protected ProtocolSchedule createProtocolSchedule() {
    return besuControllerBuilderSchedule.get(0L).createProtocolSchedule();
  }

  @Override
  protected ConsensusContext createConsensusContext(
      final Blockchain blockchain,
      final WorldStateArchive worldStateArchive,
      final ProtocolSchedule protocolSchedule) {
    return besuControllerBuilderSchedule
        .get(0L)
        .createConsensusContext(blockchain, worldStateArchive, protocolSchedule);
  }

  @Override
  protected PluginServiceFactory createAdditionalPluginServices(
      final Blockchain blockchain, final ProtocolContext protocolContext) {
    return besuControllerBuilderSchedule
        .get(0L)
        .createAdditionalPluginServices(blockchain, protocolContext);
  }

  @Override
  protected JsonRpcMethods createAdditionalJsonRpcMethodFactory(
      final ProtocolContext protocolContext) {
    return besuControllerBuilderSchedule
        .get(0L)
        .createAdditionalJsonRpcMethodFactory(protocolContext);
  }

  @Override
  protected SubProtocolConfiguration createSubProtocolConfiguration(
      final EthProtocolManager ethProtocolManager) {
    return besuControllerBuilderSchedule.get(0L).createSubProtocolConfiguration(ethProtocolManager);
  }

  @Override
  protected void validateContext(final ProtocolContext context) {
    besuControllerBuilderSchedule.get(0L).validateContext(context);
  }

  @Override
  protected String getSupportedProtocol() {
    return besuControllerBuilderSchedule.get(0L).getSupportedProtocol();
  }

  @Override
  protected EthProtocolManager createEthProtocolManager(
      final ProtocolContext protocolContext,
      final boolean fastSyncEnabled,
      final TransactionPool transactionPool,
      final EthProtocolConfiguration ethereumWireProtocolConfiguration,
      final EthPeers ethPeers,
      final EthContext ethContext,
      final EthMessages ethMessages,
      final EthScheduler scheduler,
      final List<PeerValidator> peerValidators) {
    return besuControllerBuilderSchedule
        .get(0L)
        .createEthProtocolManager(
            protocolContext,
            fastSyncEnabled,
            transactionPool,
            ethereumWireProtocolConfiguration,
            ethPeers,
            ethContext,
            ethMessages,
            scheduler,
            peerValidators);
  }

  @Override
  public BesuControllerBuilder storageProvider(final StorageProvider storageProvider) {
    besuControllerBuilderSchedule.get(0L).storageProvider(storageProvider);
    return super.storageProvider(storageProvider);
  }

  @Override
  public BesuControllerBuilder genesisConfigFile(final GenesisConfigFile genesisConfig) {
    besuControllerBuilderSchedule.get(0L).genesisConfigFile(genesisConfig);
    return super.genesisConfigFile(genesisConfig);
  }

  @Override
  public BesuControllerBuilder synchronizerConfiguration(
      final SynchronizerConfiguration synchronizerConfig) {
    besuControllerBuilderSchedule.get(0L).synchronizerConfiguration(synchronizerConfig);
    return super.synchronizerConfiguration(synchronizerConfig);
  }

  @Override
  public BesuControllerBuilder ethProtocolConfiguration(
      final EthProtocolConfiguration ethProtocolConfiguration) {
    besuControllerBuilderSchedule.get(0L).ethProtocolConfiguration(ethProtocolConfiguration);
    return super.ethProtocolConfiguration(ethProtocolConfiguration);
  }

  @Override
  public BesuControllerBuilder networkId(final BigInteger networkId) {
    besuControllerBuilderSchedule.get(0L).networkId(networkId);
    return super.networkId(networkId);
  }

  @Override
  public BesuControllerBuilder miningParameters(final MiningParameters miningParameters) {
    besuControllerBuilderSchedule.get(0L).miningParameters(miningParameters);
    return super.miningParameters(miningParameters);
  }

  @Override
  public BesuControllerBuilder messagePermissioningProviders(
      final List<NodeMessagePermissioningProvider> messagePermissioningProviders) {
    besuControllerBuilderSchedule
        .get(0L)
        .messagePermissioningProviders(messagePermissioningProviders);
    return super.messagePermissioningProviders(messagePermissioningProviders);
  }

  @Override
  public BesuControllerBuilder nodeKey(final NodeKey nodeKey) {
    besuControllerBuilderSchedule.get(0L).nodeKey(nodeKey);
    return super.nodeKey(nodeKey);
  }

  @Override
  public BesuControllerBuilder metricsSystem(final ObservableMetricsSystem metricsSystem) {
    besuControllerBuilderSchedule.get(0L).metricsSystem(metricsSystem);
    return super.metricsSystem(metricsSystem);
  }

  @Override
  public BesuControllerBuilder privacyParameters(final PrivacyParameters privacyParameters) {
    besuControllerBuilderSchedule.get(0L).privacyParameters(privacyParameters);
    return super.privacyParameters(privacyParameters);
  }

  @Override
  public BesuControllerBuilder pkiBlockCreationConfiguration(
      final Optional<PkiBlockCreationConfiguration> pkiBlockCreationConfiguration) {
    besuControllerBuilderSchedule
        .get(0L)
        .pkiBlockCreationConfiguration(pkiBlockCreationConfiguration);
    return super.pkiBlockCreationConfiguration(pkiBlockCreationConfiguration);
  }

  @Override
  public BesuControllerBuilder dataDirectory(final Path dataDirectory) {
    besuControllerBuilderSchedule.get(0L).dataDirectory(dataDirectory);
    return super.dataDirectory(dataDirectory);
  }

  @Override
  public BesuControllerBuilder clock(final Clock clock) {
    besuControllerBuilderSchedule.get(0L).clock(clock);
    return super.clock(clock);
  }

  @Override
  public BesuControllerBuilder transactionPoolConfiguration(
      final TransactionPoolConfiguration transactionPoolConfiguration) {
    besuControllerBuilderSchedule
        .get(0L)
        .transactionPoolConfiguration(transactionPoolConfiguration);
    return super.transactionPoolConfiguration(transactionPoolConfiguration);
  }

  @Override
  public BesuControllerBuilder isRevertReasonEnabled(final boolean isRevertReasonEnabled) {
    besuControllerBuilderSchedule.get(0L).isRevertReasonEnabled(isRevertReasonEnabled);
    return super.isRevertReasonEnabled(isRevertReasonEnabled);
  }

  @Override
  public BesuControllerBuilder isPruningEnabled(final boolean isPruningEnabled) {
    besuControllerBuilderSchedule.get(0L).isPruningEnabled(isPruningEnabled);
    return super.isPruningEnabled(isPruningEnabled);
  }

  @Override
  public BesuControllerBuilder pruningConfiguration(final PrunerConfiguration prunerConfiguration) {
    besuControllerBuilderSchedule.get(0L).pruningConfiguration(prunerConfiguration);
    return super.pruningConfiguration(prunerConfiguration);
  }

  @Override
  public BesuControllerBuilder genesisConfigOverrides(
      final Map<String, String> genesisConfigOverrides) {
    besuControllerBuilderSchedule.get(0L).genesisConfigOverrides(genesisConfigOverrides);
    return super.genesisConfigOverrides(genesisConfigOverrides);
  }

  @Override
  public BesuControllerBuilder gasLimitCalculator(final GasLimitCalculator gasLimitCalculator) {
    besuControllerBuilderSchedule.get(0L).gasLimitCalculator(gasLimitCalculator);
    return super.gasLimitCalculator(gasLimitCalculator);
  }

  @Override
  public BesuControllerBuilder requiredBlocks(final Map<Long, Hash> requiredBlocks) {
    besuControllerBuilderSchedule.get(0L).requiredBlocks(requiredBlocks);
    return super.requiredBlocks(requiredBlocks);
  }

  @Override
  public BesuControllerBuilder reorgLoggingThreshold(final long reorgLoggingThreshold) {
    besuControllerBuilderSchedule.get(0L).reorgLoggingThreshold(reorgLoggingThreshold);
    return super.reorgLoggingThreshold(reorgLoggingThreshold);
  }

  @Override
  public BesuControllerBuilder dataStorageConfiguration(
      final DataStorageConfiguration dataStorageConfiguration) {
    besuControllerBuilderSchedule.get(0L).dataStorageConfiguration(dataStorageConfiguration);
    return super.dataStorageConfiguration(dataStorageConfiguration);
  }

  @Override
  public BesuControllerBuilder evmConfiguration(final EvmConfiguration evmConfiguration) {
    besuControllerBuilderSchedule.get(0L).evmConfiguration(evmConfiguration);
    return super.evmConfiguration(evmConfiguration);
  }

  @VisibleForTesting
  Map<Long, BesuControllerBuilder> getBesuControllerBuilderSchedule() {
    return besuControllerBuilderSchedule;
  }
}
