package com.marklenoury.shaka

import java.io.File

import akka.actor.{ActorSystem, Props}
import com.marklenoury.shaka.services.ShakaClientService
import com.typesafe.config.ConfigFactory

case class ShakaClientOptionsConfig(
                                    configFile: Option[java.io.File] = None,
                                    mode: Option[String] = None
                                   )


object MainEntryPoint {
  def buildParser() = {
    val parser = new scopt.OptionParser[ShakaClientOptionsConfig]("Shaka") {
      opt[java.io.File]('c', "configFile")
          .valueName("/path/to/config/file")
          .action((x, c) => c.copy(configFile = Some(x)))

      opt[String]('m', "mode")
          .required()
        .valueName("client|server")
        .action((x,c) => c.copy(mode = Some(x)))
    }




    parser
  }

  def main(args: Array[String]): Unit = {
    try {
      println("Starting")
      val shakaClientOptionsConfig = this.buildParser().parse(args, ShakaClientOptionsConfig())
      if (shakaClientOptionsConfig.isEmpty) System.exit(1)

      val config = if (shakaClientOptionsConfig.get.configFile.isDefined) {
        ConfigFactory.parseFile(new File(shakaClientOptionsConfig.get.configFile.get.toString))
      } else {
        ConfigFactory.parseFile(new File("application.conf"))
      }

      val system = ActorSystem("Shaka", config)

      shakaClientOptionsConfig.get.mode.get match {
        case "client" => {
          val shakaNodeAddress = config.getString("app.client.shakaNodeAddress")
          val username = config.getString("app.client.username")
          val client = new ShakaClientService(shakaNodeAddress, system, username)
          client.run()
          sys.exit(0)
        }

        case "server" => {

          val shakaNodeRef = system.actorOf(Props[ShakaNode], "shaka-node")

        }

        case _ => {
          throw new Exception("Unknown mode")
        }
      }


    } catch {
      case ex: Exception => {
        System.err.println(ex.toString)
        sys.exit(1)
      }
    }


  }
}
