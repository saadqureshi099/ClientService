var ttoken = '[[${session.token}]]';
// Use the 'token' variable in your JavaScript code
console.log('Token:', ttoken);

sessionStorage.setItem("token", ttoken);
jwtToken = ttoken;

// Perform any necessary validation or checks
const headers = new Headers();
headers.append('Authorization', `Bearer ${jwtToken}`);

function goToHello(path) {

    fetch(path, {
        method: 'GET',
        headers: headers
    })
        .then(response => {
            console.log('sucess')
        })
        .catch(error => {
            // Handle the error
        });
}

