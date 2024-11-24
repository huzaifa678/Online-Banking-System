FROM node:alpine

WORKDIR /usr/app

COPY ./package*.json ./

RUN npm install
RUN npm install --global pm2

COPY ./ ./

RUN npm run build

EXPOSE 3000

USER node

CMD [ "pm2-runtime", "npm", "--", "start" ]