package k8stest

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.{BatchV1Api, CoreV1Api}
import io.kubernetes.client.util.Config

class K8SManagerActor extends Actor {

  override def receive: Receive = {
    case x =>
      println(x)
      sender ! Exit
  }

}

object K8SManagerActor extends App {

  println("Starting manager...")

  val config = Config.fromCluster            // Running from inside the cluster
  Configuration.setDefaultApiClient(config)  // Set it as default

  val coreApi = new CoreV1Api()
  val batchApi = new BatchV1Api()

  val managerService = coreApi.readNamespacedService("manager-actor", "k8stest", "true", null, null)

  val managerIP = managerService.getSpec.getClusterIP
  val managerPort = managerService.getSpec.getPorts.get(0).getPort

  println("Gathered the following details:", managerIP, managerPort)

  val system = ActorSystem(
    "K8STestActorSystem",
    ConfigFactory.load.withValue(
      "akka.remote.netty.tcp.bind-hostname",
      ConfigValueFactory.fromAnyRef("0.0.0.0")
    ).withValue(
      "akka.remote.netty.tcp.bind-port",
      ConfigValueFactory.fromAnyRef(6000)
    ).withValue(
        "akka.remote.netty.tcp.hostname",
        ConfigValueFactory.fromAnyRef(managerIP)
    ).withValue(
        "akka.remote.netty.tcp.port",
        ConfigValueFactory.fromAnyRef(managerPort)
    )
  )

  val managerActor = system.actorOf(Props[K8SManagerActor], "K8STestManagerActor")

}
