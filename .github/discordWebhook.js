const axios = require('axios');

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

axios.post(webhookURL, {
  name: "HeliosClient",
  embeds: [data.embeds] // send the embeds as an array
})
.then(response => console.log(response.data))
.catch((error) => {
  console.error('Error:', error);
});
