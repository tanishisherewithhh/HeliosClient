const axios = require('axios');

const webhookURL = process.env.DISCORD_WEBHOOK;
const commit = process.env.GITHUB_SHA;
const runNumber = process.env.GITHUB_RUN_NUMBER;
const repoName = process.env.GITHUB_REPOSITORY;
const pushUser = process.env.PUSH_USER;

function sendBuildStatus(status) {
  let title, description, color;
  if (status === 'success') {
    title = 'Build Successful';
    description = 'The latest build of the project was successful.';
    color = 3066993; // green
  } else if (status === 'failure') {
    title = 'Build Failed';
    description = 'The latest build of the project failed.';
    color = 15158332; // red
  }

  const data = {
    embeds: {
      title: title,
      description: description,
      color: color,
     fields: [
        {
          name: 'Push Actor',
          value: pushUser,
          inline: true
        },
        {
          name: 'Commit',
          value: '[Here](https://github.com/${repoName}/commit/${commit})',
          inline: true
        },
        {
          name: 'Run Number',
          value: runNumber,
          inline: true
        },
        {
          name: 'Build Output',
          value: '[Output](https://github.com/tanishisherewithhh/HeliosClient/.github/workflows/gradle.yml)',
          inline: true
        }
      ]
    }
  };

  axios.post(webhookURL, data)
    .then(response => console.log(response.data))
    .catch((error) => {
      console.error('Error:', error);
    });
}

// Call the function with the build status
const status = process.argv[2]; // get the status from the command line arguments
sendBuildStatus(status); // 'success' or 'failure'
