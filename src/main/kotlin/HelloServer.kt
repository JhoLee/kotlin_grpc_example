/*
 * Reference: snowdeer.github.io & grpc.io
 */
package org.example.service;

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver

import org.example.proto.Hello
import org.example.proto.HelloServiceGrpc

fun main() {
    println("[server] Starting...")
    val service = HelloService()
    val server = ServerBuilder
        .forPort(3310)
        .addService(service)
        .build()
    server.start()
    println("[server] Started.")
    println("[server] Waiting...")
    server.awaitTermination()
    println("[server] Bye.")
}

class HelloService : HelloServiceGrpc.HelloServiceImplBase() {
    override fun sayHello(request: Hello.HelloRequest?, responseObserver: StreamObserver<Hello.HelloResponse>?) {
        println("[server] sayHello(${request?.message}, ${request?.author}, ${request?.count})")

        val reply = "You called 'sayHello(${request?.message}, ${request?.author}, ${request?.count})'"
        val author = "Server"

        val response = getHelloResponse(reply, author)
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    private fun getHelloResponse(reply: String, author: String, count: Int = 1): Hello.HelloResponse {
        return Hello.HelloResponse.newBuilder()
            .setReply(reply)
            .setAuthor(author)
            .setCount(count)
            .build()
    }

}

