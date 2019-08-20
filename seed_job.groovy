pipelineJob('SimplilearnProject1PipelineJob') {
description('This pipeline job is for Simplilearn Project 1')
    definition {
        cps {
			sandbox()
			GenericTrigger {
				token("SimplilearnProject1PipelineJob")
			}
            script(readFileFromWorkspace('pipeline/java_maven_build_with_docker_deployment.groovy'))
        }
    }
}
