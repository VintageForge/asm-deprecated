// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.

plugins {
    id("java-library")
    id("maven-publish")
}

repositories { mavenCentral() }

description = "ASM deprecated classes"
group = "org.ow2.asm"
version = "9.9"

dependencies {
    implementation("org.ow2.asm:asm:$version")
    implementation("org.ow2.asm:asm-commons:$version")
    implementation("org.ow2.asm:asm-util:$version")
}

tasks {

    jar {
        manifest {
            attributes(mapOf("Implementation-Title" to project.description, "Implementation-Version" to project.version))
        }
    }

    withType<JavaCompile> {
        options.release = 11
    }

    withType<GenerateModuleMetadata> {
        enabled = false
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "vintageforge"
            url = uri("https://repo.rafi67000.xyz/vintageforge")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])

            pom {
                withXml {
                    val root = asNode()
                    val parent = root.appendNode("parent")
                    parent.appendNode("groupId", "org.ow2")
                    parent.appendNode("artifactId", "ow2")
                    parent.appendNode("version", "1.5.1")
                }

                name = artifactId
                description = project.description
                packaging = "jar"
                inceptionYear = "2000"

                licenses {
                    license {
                        name = "BSD-3-Clause"
                        url = "https://asm.ow2.io/license.html"
                    }
                }

                url = "https://asm.ow2.io/"

                mailingLists {
                    mailingList {
                        name = "ASM Users List"
                        subscribe = "https://mail.ow2.org/wws/subscribe/asm"
                        post = "asm@objectweb.org"
                        archive = "https://mail.ow2.org/wws/arc/asm/"
                    }
                    mailingList {
                        name = "ASM Team List"
                        subscribe = "https://mail.ow2.org/wws/subscribe/asm-team"
                        post = "asm-team@objectweb.org"
                        archive = "https://mail.ow2.org/wws/arc/asm-team/"
                    }
                }

                issueManagement {
                    url = "https://gitlab.ow2.org/asm/asm/issues"
                }

                scm {
                    connection = "scm:git:https://gitlab.ow2.org/asm/asm/"
                    developerConnection = "scm:git:https://gitlab.ow2.org/asm/asm/"
                    url = "https://gitlab.ow2.org/asm/asm/"
                }

                developers {
                    developer {
                        name = "Eric Bruneton"
                        id = "ebruneton"
                        email = "ebruneton@free.fr"
                        roles = listOf("Creator", "Java Developer")
                    }
                    developer {
                        name = "Eugene Kuleshov"
                        id = "eu"
                        email = "eu@javatx.org"
                        roles = listOf("Java Developer")
                    }
                    developer {
                        name = "Remi Forax"
                        id = "forax"
                        email = "forax@univ-mlv.fr"
                        roles = listOf("Java Developer")
                    }
                }

                organization {
                    name = "OW2"
                    url = "https://www.ow2.org/"
                }
            }
        }
    }
}
