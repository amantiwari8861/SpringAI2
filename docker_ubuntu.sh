## Update system packages
#sudo dnf update -y
#
## Install Docker
#sudo dnf install docker -y
#
## Start Docker service
#sudo systemctl start docker
#
## Enable Docker to start at boot
#sudo systemctl enable docker
#
## Add current user to docker group (avoid using sudo with docker)
#sudo usermod -aG docker $USER
#
## Apply group changes without logout
#newgrp docker
#
## Verify installation
#docker --version
#
## Test Docker
#docker run hello-world


## Update system
#sudo yum update -y
#
## Install Docker
#sudo yum install docker -y
#
## Start Docker service
#sudo systemctl start docker
#
## Enable Docker on system boot
#sudo systemctl enable docker
#
## Add ec2-user to docker group (run docker without sudo)
#sudo usermod -aG docker ec2-user
#
## Apply group changes
#newgrp docker
#
## Verify Docker installation
#docker --version
#
## Test Docker
#docker run hello-world


sudo apt update
sudo apt upgrade -y
sudo apt install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo \
"deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu \
$(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
docker --version
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER
newgrp docker
docker ps -a
docker run hello-world
docker compose version