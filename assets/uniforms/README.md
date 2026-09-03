# Uniformes da seleção de clubes

Na tela de seleção, cada franquia exibe apenas um uniforme principal.
Adicione a imagem PNG na raiz de `assets` usando o padrão:

- `uniforme_<chave>.png`

Chaves disponíveis:

`santos`, `rio`, `milano`, `bavaria`, `manchester`, `london`, `amsterdam`,
`madrid`, `barcelona`, `budapest`, `lisboa`, `buenosaires`, `montevideo`,
`paris`, `belfast`, `tokyo`, `seoul`, `tehran`, `baghdad`, `telaviv`,
`mexico`, `cairo`, `shanghai`, `sidney`, `newyork`, `riyadh`, `bangkok`,
`bombay`, `marseille` e `jakarta`.

Exemplo: `assets/uniforme_santos.png`.

As grafias `uniforme_amsterdan.png` e `uniforme_theran.png` também são reconhecidas
para Amsterdã Total e Tehran Lions, respectivamente.

Os arquivos `assets/uniforms/<chave>_home.png` e `assets/<chave>_home.png`
continuam funcionando como alternativas. Os arquivos `uniforme_...` têm prioridade.
Enquanto nenhuma imagem existir, a interface mostra uma camisa genérica.
