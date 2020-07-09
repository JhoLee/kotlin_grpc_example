/*
 * Reference: snowdeer.github.io & grpc.io
 */
package org.example.service

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.example.proto.Hello
import org.example.proto.HelloServiceGrpc

fun main() {
    println("[client] Starting...")
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 3310)
        .usePlaintext()
        .build()

    var choice: String
    do {
        println("(1) single->single (-1) quit")
        print("Enter the number >> ")
        choice = readLine()!!
        when (Integer.parseInt(choice)) {
            1 -> sayHello(channel)
            -1 -> println("bye.")
            else -> println("???")
        }
        // TODO
    } while (choice.isNotBlank())

}

fun getHelloRequest(message: String, author: String, count: Int = 1): Hello.HelloRequest {
    return Hello.HelloRequest.newBuilder()
        .setMessage(message)
        .setAuthor(author)
        .setCount(count)
        .build()
}

fun sayHello(channel: ManagedChannel) {
    val stub = HelloServiceGrpc.newBlockingStub(channel)

    print("Enter message >> ")
    val message = readLine()!!
    val author = "client"
    val request = getHelloRequest(message, author)

    val response = stub.sayHello(request)
    println("[client] response() -> (${response?.reply}, ${response?.author}, ${response?.count}")


}

