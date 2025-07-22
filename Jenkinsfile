pipeline {
    agent any

    environment {
        DB_HOST = 'localhost'
        DB_NAME = 'myapp'
        DB_USER = 'jenkins'
        DB_PASS = 'jenkins123'
        UPLOAD_PATH = '/home/ec2-user/uploads'
    }

    tools {
        jdk 'jdk17'
        gradle 'gradle-8.7'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'master',
                    url: 'https://github.com/Hyejin06/jenkins-samyang.git'
            }
        }

        stage('Build') {
            steps {
                bat './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                echo 'Skipping test stage in Jenkins (DB not available)'
                // bat './gradlew test'  <-- 필요시 복원
            }
        }

        stage('Deploy to AWS EC2') {
            steps {
                script {
                    writeFile file: '.env', text: """
DB_HOST=${env.DB_HOST}
DB_NAME=${env.DB_NAME}
DB_USER=${env.DB_USER}
DB_PASS=${env.DB_PASS}
UPLOAD_PATH=${env.UPLOAD_PATH}
"""
                    bat """
                        echo Step 2: Send .env to EC2
                        C:/Users/M/.ssh/pscp.exe -i C:/Users/M/.ssh/samyang.ppk -batch -hostkey "ssh-ed25519 255 SHA256:gXtUttXBU/I5kteodSG1r11d5rFS4a0v854AGjUHS10" .env ec2-user@ec2-43-201-101-246.ap-northeast-2.compute.amazonaws.com:/home/ec2-user/

                        echo Step 3: Send JAR to EC2
                        C:/Users/M/.ssh/pscp.exe -i C:/Users/M/.ssh/samyang.ppk -batch -hostkey "ssh-ed25519 255 SHA256:gXtUttXBU/I5kteodSG1r11d5rFS4a0v854AGjUHS10" build/libs/jenkins-samyang-0.0.1-SNAPSHOT.jar ec2-user@ec2-43-201-101-246.ap-northeast-2.compute.amazonaws.com:/home/ec2-user/

                        echo Step 4: Restart app on EC2
                        C:/Users/M/.ssh/plink.exe -i C:/Users/M/.ssh/samyang.ppk -batch -hostkey "ssh-ed25519 255 SHA256:gXtUttXBU/I5kteodSG1r11d5rFS4a0v854AGjUHS10" ec2-user@ec2-43-201-101-246.ap-northeast-2.compute.amazonaws.com ^
                        "pkill -f jenkins-samyang-0.0.1-SNAPSHOT.jar || true; set -a; source /home/ec2-user/.env; set +a; nohup java -jar jenkins-samyang-0.0.1-SNAPSHOT.jar > app.log 2>&1 &"

                        exit 0
                    """
                }
            }
        }
    }
}
