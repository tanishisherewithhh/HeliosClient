const fetch = require('node-fetch');

const webhookURL = 'your-webhook-url';
const commit = process.env.GITHUB_SHA;
const runNumber = process.env.GITHUB_RUN_NUMBER;
const repoName = process.env.GITHUB_REPOSITORY;
const buildOutputLink = 'your-build-output-link'; // Replace with your actual build output link

const data = {
  embeds: 
    {
      title: 'Build Successful',
      description: `The build of the project was successful. Here are some details...`,
      color: 3066993,
      fields: [
        {
          name: 'Repository',
          value: repoName,
          inline: true
        },
        {
          name: 'Commit',
          value: commit,
          inline: true
        },
        {
          name: 'Run Number',
          value: runNumber,
          inline: true
        },
        {
          name: 'Build Output',
          value: `[Link`,
          inline: true
        }
      ]
    }
  ]
};

fetch(webhookURL, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${secret}`
  },
  body: JSON.stringify(data)
})
.then(response => response.json())
.then(data => console.log(data))
.catch((error) => {
  console.error('Error:', error);
});
