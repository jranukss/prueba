//Generate ssh key
//don't use passphrase
ssh-keygen -t ed25519 -C temp@exmple.com 


//add ssh key with no .pub
ssh-add <route sshkey>

//check existing keys
ssh-add -l

//clip ssh public key
cat id_ed25519.pub

//copy in github or public repository