minikube start --cpus 4 --memory 8192
helm init
kubectl create serviceaccount --namespace kube-system tiller
kubectl create clusterrolebinding tiller-cluster-rule --clusterrole=cluster-admin --serviceaccount=kube-system:tiller
kubectl patch deploy --namespace kube-system tiller-deploy -p '{"spec":{"template":{"spec":{"serviceAccount":"tiller"}}}}'
eval $(minikube docker-env)
sbt clean docker:publishLocal
helm install --name account ./accountchart
helm status account
kubectl create -f accountchart/accounttxlog.yaml
kubectl create -f accountchart/accounttxlog-service.yaml
kubectl create -f accountchart/accountserver.yaml
kubectl create -f accountchart/accountserver-service.yaml
kubectl create -f accountchart/accounttraffic.yaml
kubectl create -f accountchart/accounttraffic-service.yaml