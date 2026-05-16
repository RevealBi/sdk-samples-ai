const reveal = require('reveal-sdk-node');
const revealAI = require('reveal-sdk-node-ai');
const express = require('express');
const cors = require('cors');
const path = require('path');
const os = require('os');

// ---------------------------------------------------------------------------
// AI provider settings
// Replace with your own API key or load from an environment variable.
// ---------------------------------------------------------------------------
const aiSettings = {
    openai: {
        ApiKey: process.env.OPENAI_API_KEY || 'YOUR_OPENAI_API_KEY',
        Model: 'gpt-4.1'
    }
};

// ---------------------------------------------------------------------------
// Reveal options
// ---------------------------------------------------------------------------
const revealOptions = {
    localFileStoragePath: path.resolve(__dirname, 'Data'),
    plugins: [
        revealAI.withOptions({
            defaultProvider: 'openai',
            settings: aiSettings,
            metadataCatalogFile: path.resolve(__dirname, 'Reveal', 'Metadata', 'catalog.json'),
            metadataManager: {
                outputPath: path.resolve(os.homedir(), 'AImetadata'),
            },
            callbacks: {
                contextManagerProvider: async (userContext, message) => {
                    return '';
                },
                aiProvider: async (userContext, message) => {
                    return '';
                }
            }
        })
    ]
};

// ---------------------------------------------------------------------------
// Express app
// ---------------------------------------------------------------------------
const app = express();
app.use(cors());
app.use('/', reveal(revealOptions));

const PORT = parseInt(process.env.PORT || '5111', 10);
app.listen(PORT, () => {
    console.log(`Reveal AI Insights sample (Node) running on http://localhost:${PORT}`);
});
