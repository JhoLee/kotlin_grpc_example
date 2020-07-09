/*
 * Reference: snowdeer.github.io & grpc.io
 */
package org.example.service;

import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver

import org.example.proto.Hello
import org.example.proto.HelloServiceGrpc
import java.lang.Thread.sleep

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
    private fun getHelloResponse(reply: String, author: String, count: Int = 1): Hello.HelloResponse {
        return Hello.HelloResponse.newBuilder()
            .setReply(reply)
            .setAuthor(author)
            .setCount(count)
            .build()
    }

    override fun sayHello(request: Hello.HelloRequest?, responseObserver: StreamObserver<Hello.HelloResponse>?) {
        println("[server] sayHello(${request?.message}, ${request?.author}, ${request?.count})")

        val reply = "You called 'sayHello(${request?.message}, ${request?.author}, ${request?.count})'"
        val author = "Server"

        val response = getHelloResponse(reply, author)
        responseObserver?.onNext(response)
        responseObserver?.onCompleted()
    }

    override fun lotsOfReplies(request: Hello.HelloRequest?, responseObserver: StreamObserver<Hello.HelloResponse>?) {
        println("[server] lotsOfReplies(${request?.message}, ${request?.author}, ${request?.count})")

        val reply = "You called 'lotsOfReplies(${request?.message}, ${request?.author}, ${request?.count})'"
        val author = "Server"
        val count = request?.count ?: 0

        for (i in 1..count) {
            val response = getHelloResponse("[${count}] $reply", author, i)
            responseObserver?.onNext(response)

            sleep(500)
        }
        responseObserver?.onCompleted()
    }

    override fun lotsOfGreetings(responseObserver: StreamObserver<Hello.HelloResponse>?): StreamObserver<Hello.HelloRequest> {

        return object : StreamObserver<Hello.HelloRequest> {
            override fun onNext(value: Hello.HelloRequest?) {
                println("[server] lotsOfGreetings() - onNext(${value?.message}, ${value?.author}, ${value?.count})")
            }

            override fun onError(t: Throwable?) {
                println("[server] lotsOfGreetings() - onError()")
            }

            override fun onCompleted() {
                println("[server] lotsOfGreetings() - onCompleted()")

                val response = getHelloResponse("exampleReply", "server")
                responseObserver?.onNext(response)
                responseObserver?.onCompleted()
            }
        }
    }

    override fun bidiHello(responseObserver: StreamObserver<Hello.HelloResponse>?): StreamObserver<Hello.HelloRequest> {
        return object : StreamObserver<Hello.HelloRequest> {
            override fun onNext(value: Hello.HelloRequest?) {
                println("[server] bidiHello() - onNext(${value?.message}, ${value?.author}, ${value?.count}")
                val response = getHelloResponse("exampleReply", "server", value?.count ?: 0)
                responseObserver?.onNext(response)
            }

            override fun onError(t: Throwable?) {
                println("[server] bidiHello() - onError()")
            }

            override fun onCompleted() {
                println("[server] bidiHello() - onCompleted()")
                responseObserver?.onCompleted()
            }
        }
    }
}

