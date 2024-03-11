const fetch = require('node-fetch');

const webhookURL = process.env.DISCORD_WEBHOOK;
const commit = process.env.GITHUB_SHA;
const runNumber = process.env.GITHUB_RUN_NUMBER;
const repoName = process.env.GITHUB_REPOSITORY;

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
          value: "https://github.com/tanishisherewithhh/HeliosClient/.github/workflows/gradle.yml",
          inline: true
        }
      ]
    }
};

fetch(webhookURL, {
  method: 'POST',
  name: "HeliosClient",
  body: JSON.stringify(data)
})
.then(response => response.json())
.then(data => console.log(data))
.catch((error) => {
  console.error('Error:', error);
});
