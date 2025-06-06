multibranchPipelineJob('trace-llm-pr-checks') {
    description('Runs build checks on Pull Requests')
    
    branchSources {
        branchSource {
            source {
                github {
                    id('trace-llm-pr-checks')
                    credentialsId('github-pat')
                    configuredByUrl(true)
                    repoOwner('cyse7125-sp25-team03')
                    repository('trace-llm')
                    repositoryUrl('https://github.com/cyse7125-sp25-team03/trace-llm.git')
                }
            }
        }
    }
    
    configure {
        def traits = it / sources / data / 'jenkins.branch.BranchSource' / source / traits
        traits << 'org.jenkinsci.plugins.github__branch__source.OriginPullRequestDiscoveryTrait' {
            strategyId(1)
        }
        traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
            strategyId(1)
            trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
        }
        
        // Configure automatic PR build trigger
        it / triggers << 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
            spec('H/5 * * * *')
            interval('300000')
        }
        
        // Enable build strategies for PRs
        def buildStrategiesNode = it / buildStrategies
        buildStrategiesNode << 'jenkins.branch.buildstrategies.basic.ChangeRequestBuildStrategy' {
            ignoreTargetOnlyChanges(true)
            ignoreUntrustedChanges(false)
        }
    }
    
    factory {
        workflowBranchProjectFactory {
            scriptPath('Jenkinsfile.prcheck')
        }
    }
    
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(20)
        }
    }
}