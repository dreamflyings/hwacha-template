package example

import chisel3._
import freechips.rocketchip.config.{Parameters, Config}
import freechips.rocketchip.subsystem.{WithRoccExample, WithNMemoryChannels, WithNBigCores, WithRV32, WithNBanks, WithExtMemSize}
import freechips.rocketchip.diplomacy.{LazyModule, ValName}
import freechips.rocketchip.devices.tilelink.BootROMParams
import freechips.rocketchip.tile.XLen
import testchipip._

class WithBootROM extends Config((site, here, up) => {
  case BootROMParams => BootROMParams(
    contentFileName = s"./bootrom/bootrom.rv${site(XLen)}.img")
})

object ConfigValName {
  implicit val valName = ValName("TestHarness")
}
import ConfigValName._

class WithExampleTop extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    Module(LazyModule(new ExampleTop()(p)).module)
  }
})

class WithPWM extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) =>
    Module(LazyModule(new ExampleTopWithPWMTL()(p)).module)
})

class WithPWMAXI4 extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) =>
    Module(LazyModule(new ExampleTopWithPWMAXI4()(p)).module)
})

class WithBlockDeviceModel extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    val top = Module(LazyModule(new ExampleTopWithBlockDevice()(p)).module)
    top.connectBlockDeviceModel()
    top
  }
})

class WithSimBlockDevice extends Config((site, here, up) => {
  case BuildTop => (clock: Clock, reset: Bool, p: Parameters) => {
    val top = Module(LazyModule(new ExampleTopWithBlockDevice()(p)).module)
    top.connectSimBlockDevice(clock, reset)
    top
  }
})

class ExampleHwachaConfig extends Config(
  new WithBootROM ++
  new hwacha.ISCA2016Config)

class SingleBankHwachaConfig extends Config(
  new WithNBanks(1) ++
  new WithBootROM ++
  new hwacha.ISCA2016Config)

class ExampleHwacha4LaneConfig extends Config(
  new WithBootROM ++
  new hwacha.ISCA2016L4Config)

class BaseExampleConfig extends Config(
  new WithBootROM ++
  new freechips.rocketchip.system.DefaultConfig)

class DefaultExampleConfig extends Config(
  new WithExampleTop ++ new BaseExampleConfig)

class RoccExampleConfig extends Config(
  new WithRoccExample ++ new DefaultExampleConfig)

class PWMConfig extends Config(new WithPWM ++ new BaseExampleConfig)

class PWMAXI4Config extends Config(new WithPWMAXI4 ++ new BaseExampleConfig)

class SimBlockDeviceConfig extends Config(
  new WithBlockDevice ++ new WithSimBlockDevice ++ new BaseExampleConfig)

class BlockDeviceModelConfig extends Config(
  new WithBlockDevice ++ new WithBlockDeviceModel ++ new BaseExampleConfig)

class WithTwoTrackers extends WithNBlockDeviceTrackers(2)
class WithFourTrackers extends WithNBlockDeviceTrackers(4)

class WithTwoMemChannels extends WithNMemoryChannels(2)
class WithFourMemChannels extends WithNMemoryChannels(4)

// 16GB of off chip memory
class BigMemoryConfig extends Config(
  new WithExtMemSize((1<<30) * 16L) ++ new DefaultExampleConfig)
// 1GB of off chip memory
class GB1MemoryConfig extends Config(
  new WithExtMemSize((1<<30) * 1L) ++ new DefaultExampleConfig)
class GB2MemoryConfig extends Config(
  new WithExtMemSize((1<<30) * 2L) ++ new DefaultExampleConfig)
class GB4MemoryConfig extends Config(
  new WithExtMemSize((1<<30) * 4L) ++ new DefaultExampleConfig)
class GB8MemoryConfig extends Config(
  new WithExtMemSize((1<<30) * 8L) ++ new DefaultExampleConfig)

class DualCoreConfig extends Config(
  // Core gets tacked onto existing list
  new WithNBigCores(2) ++ new DefaultExampleConfig)

class RV32ExampleConfig extends Config(
  new WithRV32 ++ new DefaultExampleConfig)
