pipeline {
    agent {
        label 'openjdk8bot'
    }

    tools {
        maven 'maven-3.6'
        jdk 'adoptopenjdk-jdk8'
    }
    environment {
        MAVEN_OPTS='-Djava.awt.headless=true -Xmx4096m'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
                sh 'mvn -version'
                sh 'java -version'
                sh 'git --version'
            }
        }
        stage ('Build') {
            steps {
               echo 'Unit testing'
               sh 'mvn -B -C -Poracle,mssql clean test-compile'
            }
        }
        stage ('Integration Test') {
            steps {
                echo 'Integration testing'
                sh 'mvn -B -C -Pintegration-tests,oracle,mssql install'
            }
            post {
                always {
                    junit '**/target/*-reports/*.xml'
                }
            }
        }
        stage ('Quality Checks') {
            when {
                branch 'master'
            }
            steps {
                echo 'Quality checking'
                sh 'mvn -B -C -fae -Poracle,mssql findbugs:findbugs checkstyle:checkstyle javadoc:javadoc'
            }
            post {
                success {
                    findbugs canComputeNew: false, defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', pattern: '**/findbugsXml.xml', unHealthy: ''
                    checkstyle canComputeNew: false, canRunOnFailed: true, defaultEncoding: '', healthy: '', pattern: '**/checkstyle-result.xml', unHealthy: ''
                }
            }
        }
        stage ('Acceptance Test') {
            steps {
                echo 'Preparing test environment'
                echo 'Download SUT deegree workspace'
                echo 'Start SUT deegree webapp with test configuration'
                echo 'Run FAT'
            }
            post {
                success {
                    echo 'FAT passed successfully'
                }
            }
        }
        stage ('Release') {
            when {
                branch 'master'
            }
            steps {
                echo 'Prepare release version'
                echo 'Build and publish documentation'
                sh 'mvn -pl :deegree-webservices-handbook -Phandbook package'
                sh 'mvn site -Psite-all-reports'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/deegree-webservices-*.war,**/target/deegree-webservices-handbook-*.zip', fingerprint: true, followSymlinks: false, onlyIfSuccessful: true
                }
            }
        }
        stage ('Deploy') {
            when {
                branch 'master'
            }
            // install current release version on demo.deegree.org
            steps {
                echo 'Deploying to demo.deegree.org...'
                echo 'Running smoke tests...'
            }
        }
    }
}
