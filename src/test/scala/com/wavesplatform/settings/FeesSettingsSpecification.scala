package com.wavesplatform.settings

import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

class FeesSettingsSpecification extends FlatSpec with Matchers {
  "FeesSettings" should "read values" in {
    val config = ConfigFactory.parseString("""waves {
        |  network.file = "xxx"
        |  fees {
        |    payment.COF = 100000
        |    issue.COF = 100000000
        |    transfer.COF = 100000
        |    reissue.COF = 100000
        |    burn.COF = 100000
        |    exchange.COF = 100000
        |  }
        |  miner.timeout = 10
        |}
      """.stripMargin).resolve()

    val settings = FeesSettings.fromConfig(config)
    settings.fees.size should be(6)
    settings.fees(2) should be(List(FeeSettings("COF", 100000)))
    settings.fees(3) should be(List(FeeSettings("COF", 100000000)))
    settings.fees(4) should be(List(FeeSettings("COF", 100000)))
    settings.fees(5) should be(List(FeeSettings("COF", 100000)))
    settings.fees(6) should be(List(FeeSettings("COF", 100000)))
    settings.fees(7) should be(List(FeeSettings("COF", 100000)))
  }

  it should "combine read few fees for one transaction type" in {
    val config = ConfigFactory.parseString("""waves.fees {
        |  payment {
        |    COF0 = 0
        |  }
        |  issue {
        |    COF1 = 111
        |    COF2 = 222
        |    COF3 = 333
        |  }
        |  transfer {
        |    COF4 = 444
        |  }
        |}
      """.stripMargin).resolve()

    val settings = FeesSettings.fromConfig(config)
    settings.fees.size should be(3)
    settings.fees(2).toSet should equal(Set(FeeSettings("COF0", 0)))
    settings.fees(3).toSet should equal(Set(FeeSettings("COF1", 111), FeeSettings("COF2", 222), FeeSettings("COF3", 333)))
    settings.fees(4).toSet should equal(Set(FeeSettings("COF4", 444)))
  }

  it should "allow empty list" in {
    val config = ConfigFactory.parseString("waves.fees {}".stripMargin).resolve()

    val settings = FeesSettings.fromConfig(config)
    settings.fees.size should be(0)
  }

  it should "override values" in {
    val config = ConfigFactory
      .parseString("""waves.fees {
        |  payment.COF1 = 1111
        |  reissue.COF5 = 0
        |}
      """.stripMargin)
      .withFallback(
        ConfigFactory.parseString("""waves.fees {
          |  payment.COF = 100000
          |  issue.COF = 100000000
          |  transfer.COF = 100000
          |  reissue.COF = 100000
          |  burn.COF = 100000
          |  exchange.COF = 100000
          |}
        """.stripMargin)
      )
      .resolve()

    val settings = FeesSettings.fromConfig(config)
    settings.fees.size should be(6)
    settings.fees(2).toSet should equal(Set(FeeSettings("COF", 100000), FeeSettings("COF1", 1111)))
    settings.fees(5).toSet should equal(Set(FeeSettings("COF", 100000), FeeSettings("COF5", 0)))
  }

  it should "fail on incorrect long values" in {
    val config = ConfigFactory.parseString("""waves.fees {
        |  payment.COF=N/A
        |}""".stripMargin).resolve()
    intercept[WrongType] {
      FeesSettings.fromConfig(config)
    }
  }

  it should "not fail on long values as strings" in {
    val config   = ConfigFactory.parseString("""waves.fees {
        |  transfer.COF="1000"
        |}""".stripMargin).resolve()
    val settings = FeesSettings.fromConfig(config)
    settings.fees(4).toSet should equal(Set(FeeSettings("COF", 1000)))
  }

  it should "fail on unknown transaction type" in {
    val config = ConfigFactory.parseString("""waves.fees {
        |  shmayment.COF=100
        |}""".stripMargin).resolve()
    intercept[NoSuchElementException] {
      FeesSettings.fromConfig(config)
    }
  }

  it should "override values from default config" in {
    val defaultConfig = ConfigFactory.load()
    val config        = ConfigFactory.parseString("""
        |waves.fees {
        |  issue {
        |    COF = 200000000
        |  }
        |  transfer {
        |    COF = 300000
        |    "6MPKrD5B7GrfbciHECg1MwdvRUhRETApgNZspreBJ8JL" = 1
        |  }
        |  reissue {
        |    COF = 400000
        |  }
        |  burn {
        |    COF = 500000
        |  }
        |  exchange {
        |    COF = 600000
        |  }
        |  lease {
        |    COF = 700000
        |  }
        |  lease-cancel {
        |    COF = 800000
        |  }
        |  create-alias {
        |    COF = 900000
        |  }
        |  mass-transfer {
        |    COF = 10000
        |  }
        |  data {
        |    COF = 200000
        |  }
        |  set-script {
        |    COF = 300000
        |  }
        |  sponsor-fee {
        |    COF = 400000
        |  }
        |}
      """.stripMargin).withFallback(defaultConfig).resolve()
    val settings      = FeesSettings.fromConfig(config)
    settings.fees.size should be(12)
    settings.fees(3).toSet should equal(Set(FeeSettings("COF", 200000000)))
    settings.fees(4).toSet should equal(Set(FeeSettings("COF", 300000), FeeSettings("6MPKrD5B7GrfbciHECg1MwdvRUhRETApgNZspreBJ8JL", 1)))
    settings.fees(5).toSet should equal(Set(FeeSettings("COF", 400000)))
    settings.fees(6).toSet should equal(Set(FeeSettings("COF", 500000)))
    settings.fees(7).toSet should equal(Set(FeeSettings("COF", 600000)))
    settings.fees(8).toSet should equal(Set(FeeSettings("COF", 700000)))
    settings.fees(9).toSet should equal(Set(FeeSettings("COF", 800000)))
    settings.fees(10).toSet should equal(Set(FeeSettings("COF", 900000)))
    settings.fees(11).toSet should equal(Set(FeeSettings("COF", 10000)))
    settings.fees(12).toSet should equal(Set(FeeSettings("COF", 200000)))
    settings.fees(13).toSet should equal(Set(FeeSettings("COF", 300000)))
    settings.fees(14).toSet should equal(Set(FeeSettings("COF", 400000)))
  }
}
