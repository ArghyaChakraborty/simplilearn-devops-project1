node('master') {
currentBuild.result = 'SUCCESS'
def jobStatus
def mvnHome = tool 'Simplilearn-Maven'
def dockerhubId = 'arghya1988'

  try {    
   
    stage("Checkout Stage"){
      timestamps {
		cleanWs()
        try {
            checkout([$class: 'GitSCM',
                branches: [[name: 'master']],
                doGenerateSubmoduleConfigurations: false,
                extensions: [[$class: 'RelativeTargetDirectory',relativeTargetDir: "demo_java"]],
                submoduleCfg: [],
                userRemoteConfigs: [[credentialsId: 'arghyaGithubId',name: 'origin',
                url: 'https://github.com/ArghyaChakraborty/demo-java.git']]])

            currentBuild.result = 'SUCCESS'
        } catch(Exception err) {
            println err
            currentBuild.result = 'FAILURE'
            sh "exit 1"
        }
      }
    }
    
    if(currentBuild.result.equals("SUCCESS")) {
        stage('Build Stage') {
            timestamps {
                // Run the maven build
                withEnv(["MVN_HOME=$mvnHome"]) {
                    jobStatus = sh(returnStatus: true, script: 'cd ${WORKSPACE}/demo_java; "$MVN_HOME/bin/mvn" -Dmaven.test.failure.ignore clean package')
                    if(jobStatus == 0) {
                        currentBuild.result = 'SUCCESS'
                    } else {
                        currentBuild.result = 'FAILURE'
                        sh "exit 1"
                    }
                }
            }
        }
    }
	
    if(currentBuild.result.equals("SUCCESS")) {
	    stage("Deployment Stage"){
            timestamps {
                try {
                    checkout([$class: 'GitSCM',
                        branches: [[name: 'master']],
                        doGenerateSubmoduleConfigurations: false,
                        extensions: [[$class: 'RelativeTargetDirectory',relativeTargetDir: "simplilearn-devops-project1"]],
                        submoduleCfg: [],
                        userRemoteConfigs: [[credentialsId: 'arghyaGithubId',name: 'origin',
                        url: 'https://github.com/ArghyaChakraborty/simplilearn-devops-project1.git']]])               

					    sh(script: "mv ${WORKSPACE}/demo_java/target/demo.war ${WORKSPACE}/simplilearn-devops-project1/docker/")
					    withDockerRegistry(registry: [credentialsId: 'dockerhubId']) {
						    def customImage = docker.build("${dockerhubId}/simplilearn-project1:${env.BUILD_NUMBER}", "${WORKSPACE}/simplilearn-devops-project1/docker/")
						    customImage.push();
					    }
						
                    currentBuild.result = 'SUCCESS'
                } catch(Exception err) {
                    println err
                    currentBuild.result = 'FAILURE'
                    sh "exit 1"
                }
            }
        }
    }
	
  } catch(err) {
    println err
  }
}

