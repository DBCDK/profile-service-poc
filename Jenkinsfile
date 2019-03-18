if (env.BRANCH_NAME == 'master') {
    properties([
        disableConcurrentBuilds(),
    ])
}
pipeline {
    agent { label "devel8" }
    tools {
        maven "maven 3.5"
    }
    environment {
        MAVEN_OPTS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
    }
    triggers {
        pollSCM("H/3 * * * *")
    }
    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: "", artifactNumToKeepStr: "", daysToKeepStr: "30", numToKeepStr: "30"))
        timestamps()
    }
    stages {
        stage("build") {
            steps {
                // Fail Early..
                script {
                    if (! env.BRANCH_NAME) {
                        currentBuild.result = Result.ABORTED
                        throw new hudson.AbortException('Job Started from non MultiBranch Build')
                    } else {
                        println(" Building BRANCH_NAME == ${BRANCH_NAME}")
                    }
                }

                sh """
                    rm -rf \$WORKSPACE/.repo/dk/dbc
                    mvn -B -Dmaven.repo.local=\$WORKSPACE/.repo clean
                    mvn -B -Dmaven.repo.local=\$WORKSPACE/.repo install javadoc:aggregate -Dsurefire.useFile=false -Dmaven.test.failure.ignore
                """
                script {
                    junit testResults: '**/target/surefire-reports/TEST-*.xml'

                    def java = scanForIssues tool: [$class: 'Java']
                    def javadoc = scanForIssues tool: [$class: 'JavaDoc']

                    publishIssues issues:[java,javadoc], unstableTotalAll:1
                }
            } 
        }

        stage("analysis") {
            steps {
                sh """
                    mvn -B -Dmaven.repo.local=\$WORKSPACE/.repo pmd:pmd pmd:cpd findbugs:findbugs
                """

                script {
                    def pmd = scanForIssues tool: [$class: 'Pmd'], pattern: '**/target/pmd.xml'
                    publishIssues issues:[pmd], unstableTotalAll:1

                    def cpd = scanForIssues tool: [$class: 'Cpd'], pattern: '**/target/cpd.xml'
                    publishIssues issues:[cpd]

                    def findbugs = scanForIssues tool: [$class: 'FindBugs'], pattern: '**/target/findbugsXml.xml'
                    publishIssues issues:[findbugs], unstableTotalAll:1
                }
            }
        }

        stage("docker") {
            steps {
                script {
                    def allDockerFiles = findFiles glob: '**/Dockerfile'
                    def dockerFiles = allDockerFiles.findAll { f -> f.path.endsWith("src/main/docker/Dockerfile") }
                    def version = readMavenPom().version

                    for (def f : dockerFiles) {
                        def dirName = f.path.take(f.path.length() - "src/main/docker/Dockerfile".length())
                        if ( dirName == '' )
                            dirName = '.'
                        dir(dirName) {
                            modulePom = readMavenPom file: 'pom.xml'
                            def projectArtifactId = modulePom.getArtifactId()
                            if( !projectArtifactId ) {
                                throw new hudson.AbortException("Unable to find module ArtifactId in ${dirName} remember to add a <ArtifactId> element")
                            }

                            def imageName = "${projectArtifactId}-${version}".toLowerCase()
                            if (! env.CHANGE_BRANCH) {
                                imageLabel = env.BRANCH_NAME
                            } else {
                                imageLabel = env.CHANGE_BRANCH
                            }
                            if ( ! (imageLabel ==~ /master|trunk/) ) {
                                println("Using branch_name ${imageLabel}")
                                imageLabel = imageLabel.split(/\//)[-1]
                                imageLabel = imageLabel.toLowerCase()
                            } else {
                                println(" Using Master branch ${BRANCH_NAME}")
                                imageLabel = env.BUILD_NUMBER
                            }

                            println("In ${dirName} build ${projectArtifactId} as ${imageName}:$imageLabel")

                            sh 'cp -af src/main/docker/. target/'
                            def app = docker.build("$imageName:${imageLabel}".toLowerCase(), '--pull --no-cache target')

                            if (currentBuild.resultIsBetterOrEqualTo('SUCCESS')) {
                                docker.withRegistry('https://docker-os.dbc.dk', 'docker') {
                                    app.push()
                                    if (env.BRANCH_NAME ==~ /master|trunk/) {
                                        app.push "latest"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
