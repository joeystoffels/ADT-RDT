def buildClosure = {
    sh "./gradlew clean build --info --stacktrace -DBUILD_NUMBER=${env.BUILD_NUMBER} -DBUILD_TIMESTAMP=${env.BUILD_TIMESTAMP} -DBRANCH_NAME=${env.BRANCH_NAME}"
}

def buildParameterMap = [:]
buildParameterMap['appName'] = 'exe-database-performance-test'
buildParameterMap['buildClosure'] = buildClosure
buildParameterMap['jenkinsNodeLabel'] = 'jdk11'

buildParameterMap['deploymentStrategy'] = [
    "develop":["exe-test",
]

buildAndDeployGeneric(buildParameterMap)