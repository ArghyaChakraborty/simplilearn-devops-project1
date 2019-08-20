pipelineJob('SimplilearnProject1PipelineJob') {
description('This pipeline job is for Simplilearn Project 1')
    definition {
        cps {
			sandbox()
            script(readFileFromWorkspace('pipeline/java_maven_build_with_docker_deployment.groovy'))
        }
    }
}
